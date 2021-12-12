package com.borets.pfa.service

import com.borets.pfa.entity.account.Account
import com.borets.pfa.entity.project.DiscoveryProject
import com.borets.pfa.entity.project.Project
import com.borets.pfa.entity.project.ProjectAssignment
import com.haulmont.cuba.core.global.CommitContext
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.View
import com.haulmont.cuba.core.global.ViewBuilder
import com.haulmont.cuba.security.app.Authenticated
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime

@Service(DiscoveryProjectsImportService.NAME)
class DiscoveryProjectsImportServiceBean : DiscoveryProjectsImportService {

    @Autowired
    private lateinit var dataManager: DataManager

    @Autowired
    private lateinit var metadata: com.haulmont.cuba.core.global.Metadata

    @Autowired
    private lateinit var log: org.slf4j.Logger

    @Authenticated
    override fun import() {
        log.info("Start importing Projects from Discovery DSI...")

        val stats = doImport()
        log.info("Import from Discovery DSI is finished.\n $stats")

        val createdAssignmentsCount = createProjectAssignments(stats.newEntities)
        log.info("Created {} project assignments after import.", createdAssignmentsCount)
    }

    private fun doImport(): DiscoveryProjectsImportStatistics {
        val startTime = LocalDateTime.now()
        // WARNING:
        // external view contains poorly prepared data (leading spaces, garbage etc.).
        // Leave it as is, do not try to clean up or sanitize.
        val discoveryProjects = dataManager
            .load(DiscoveryProject::class.java)
            .list()

        val projects = dataManager
            .load(Project::class.java)
            .softDeletion(false)
            .list()

        val ctx = CommitContext()

        // In case if no data in database yet - faster copying DiscoveryProject entities (without any existence checks)
        if (projects.isEmpty()) {
            discoveryProjects.forEach { ctx.addInstanceToCommit(createProject(it)) }
            dataManager.commit(ctx)
            return DiscoveryProjectsImportStatistics(
                0, 0, discoveryProjects.size,
                discoveryProjects.map { it.wellId }.toMutableList(),
                startTime = startTime, elapsedTime = Duration.between(startTime, LocalDateTime.now())
            )
        }

        // It is crucially important for correctness of the merge to sort both collections
        discoveryProjects.sortWith { o1, o2 -> StringUtils.compare(o1.wellId, o2.wellId) }
        projects.sortWith { o1, o2 -> StringUtils.compare(o1.wellId, o2.wellId) }

        val newProjects = arrayListOf<Project>()
        val stats =
            DiscoveryProjectsImportStatistics(projects.size, projects.count { it.isDeleted }, discoveryProjects.size)

        var discoveryProjectsIndex = 0;
        var projectsIndex = 0;

        val discoveryProjectsSize = discoveryProjects.size
        val projectsSize = projects.size

        while (projectsIndex < projectsSize) {
            val project = projects[projectsIndex]
            projectsIndex++

            if (discoveryProjectsIndex >= discoveryProjectsSize) {
                if (!project.isDeleted) {
                    log.info("(*) Project not found in discoveryProjects and will be deleted -- WELL ID = ${project.wellId}")
                    ctx.addInstanceToRemove(project)
                    stats.addDeletedEntity(project.wellId)
                }
                continue
            }

            while (discoveryProjectsIndex < discoveryProjectsSize) {
                val discoveryProject = discoveryProjects[discoveryProjectsIndex]
                val comparisonResult = StringUtils.compare(discoveryProject.wellId, project.wellId)
                if (comparisonResult > 0) {
                    if (!project.isDeleted) {
                        log.info("(**) Project not found in discoveryProjects and will be deleted -- WELL ID = ${project.wellId}")
                        ctx.addInstanceToRemove(project)
                        stats.addDeletedEntity(project.wellId)
                    }
                    break
                }
                if (comparisonResult < 0) {
                    val newProject = createProject(discoveryProject)
                    newProjects.add(newProject)
                    stats.addCreatedEntity(newProject.wellId)
                    discoveryProjectsIndex++
                }
                if (comparisonResult == 0) {
                    if (updateProject(project, discoveryProject)) {
                        ctx.addInstanceToCommit(project)
                        stats.addUpdatedEntity(project.wellId)
                    }
                    discoveryProjectsIndex++
                    break
                }
            }
        }

        while (discoveryProjectsIndex < discoveryProjectsSize) {
            val project = createProject(discoveryProjects[discoveryProjectsIndex])
            newProjects.add(project)
            stats.addCreatedEntity(project.wellId)
            discoveryProjectsIndex++
        }

        newProjects.forEach {
            log.debug("Add - ${it.wellId}, ${it.well}, ${it.region}, ${it.customerNo}")
            ctx.addInstanceToCommit(it)
        }
        dataManager.commit(ctx)

        stats.startTime = startTime
        stats.elapsedTime = Duration.between(startTime, LocalDateTime.now())
        return stats
    }

    private fun createProject(dp: DiscoveryProject): Project {
        val newProject = metadata.create(Project::class.java)
        newProject.wellId = dp.wellId
        updateProject(newProject, dp)
        return newProject
    }

    private fun updateProject(project: Project, discoveryProject: DiscoveryProject): Boolean {
        var hasChanges = false
        if (project.well != discoveryProject.well
            || project.customerNo != discoveryProject.customerNo
            || project.region != discoveryProject.region
            || project.wellApi != discoveryProject.power
        ) {
            hasChanges = true
        }

        if (hasChanges) {
            project.well = discoveryProject.well
            project.customerNo = discoveryProject.customerNo
            project.region = discoveryProject.region
            project.wellApi = discoveryProject.power
        }
        return hasChanges
    }

    /**
     * Creating ProjectAssignments.
     */
    private fun createProjectAssignments(projectWellIds: List<String?>): Int {
        if (projectWellIds.isEmpty()) {
            return 0
        }

        // loading all projects without assignment in single query (only for faster processing)...
        val projectsWithoutAssignments = dataManager.load(Project::class.java)
            .query("select p from pfa_Project  p where not exists " +
                    "(select pa from pfa_ProjectAssignment pa where pa.project = p)")
            .view(
                ViewBuilder.of(Project::class.java)
                    .addView(View.LOCAL)
                    .addAll("assignments")
                    .build()
            )
            .list()

        if (projectsWithoutAssignments.isEmpty()) {
            return 0
        }

        var count = 0
        val commitContext = CommitContext()
        val dateStart = LocalDateTime.now()

        for (project in projectsWithoutAssignments) {
            if (!projectWellIds.contains(project.wellId)) {
                continue
            }
            try {
                    val accounts = dataManager.load(Account::class.java)
                        .query("select a from pfa_Account a where a.customerId = :customerNo")
                        .parameter("customerNo", project.customerNo)
                        .list()

                    if (accounts.size == 1) {
                        // Found project without ProjectAssignment and
                        // there is only one Account with CustomerId same as Project's customerNo.
                        // Creating ProjectAssignment for Project and Account...

                        val account = accounts[0]
                        log.info("Creating project assignment for project {} and account {}...",
                            project.wellId, account.name)

                        val projectAssignment = metadata.create(ProjectAssignment::class.java)
                        projectAssignment.project = project
                        projectAssignment.account = account
                        projectAssignment.dateStart = dateStart

                        commitContext.addInstanceToCommit(projectAssignment)
                        count++
                    }


            } catch (exc: IllegalStateException) {
                log.info("Could not create ProjectAssignment for project with wellId {}", project.wellId)
            }
        }

        dataManager.commit(commitContext)
        return count
    }
}