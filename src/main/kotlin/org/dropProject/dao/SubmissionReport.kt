/*-
 * ========================LICENSE_START=================================
 * DropProject
 * %%
 * Copyright (C) 2019 Pedro Alves
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.dropProject.dao

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

enum class Indicator(val code: String, val description: String) {
    PROJECT_STRUCTURE("PS", "Project Structure"),
    COMPILATION("C", "Compilation"),
    CHECKSTYLE("CS", "Code Quality (Checkstyle)"),
    TEACHER_UNIT_TESTS("TT", "Teacher Unit Tests"),
    STUDENT_UNIT_TESTS("ST", "Student Unit Tests"),
    HIDDEN_UNIT_TESTS("HT", "Teacher Hidden Unit Tests");

    companion object {
        fun getIndicator(code: String) : Indicator {

            for (indicator in Indicator.values()) {
                if (code.equals(indicator.code)) {
                    return indicator
                }
            }
            throw IllegalArgumentException("No matching Indicator for code ${code}")
        }

    }
}


@Entity
data class SubmissionReport(
        @Id @GeneratedValue
        val id: Long = 0,
        val submissionId: Long = 0,  // FK for Submission.id
        val reportKey: String,  // Indicator::code
        val reportValue: String,  // TODO: Change this to enum (right now you have "OK", "NOK" and "Not Enough Tests")
        val reportProgress: Int? = null,
        val reportGoal: Int? = null,

        @Transient var assignment : Assignment? = null
) {

    val indicator: Indicator
        get() {
            return Indicator.getIndicator(reportKey)
        }

    fun progressSummary(isTeacher: Boolean): String? {

        if (!showItem(isTeacher)) {
            return null
        }

        if (!isTeacher && indicator == Indicator.HIDDEN_UNIT_TESTS) {
            if (assignment?.hiddenTestsVisibility == TestVisibility.SHOW_PROGRESS) {
                reportProgress?.toString()
            } else {
                return null
            }
        }

        return if (reportProgress != null) {
                if (reportGoal != null) {
                    "$reportProgress / $reportGoal"
                } else {
                    reportProgress.toString()
                }
            } else {
                null
            }
    }

    fun showIcon(isTeacher: Boolean): Boolean {

        if (!showItem(isTeacher)) {
            return false
        }

        if (reportProgress == null && reportGoal == null) {
            return true
        }

        if (!isTeacher && indicator == Indicator.HIDDEN_UNIT_TESTS &&
                assignment?.hiddenTestsVisibility == TestVisibility.SHOW_OK_NOK) {
            return true
        }

        return false
    }

    fun showItem(isTeacher: Boolean): Boolean {

        if (!isTeacher && indicator == Indicator.HIDDEN_UNIT_TESTS &&
                (assignment?.hiddenTestsVisibility == null ||
                 assignment?.hiddenTestsVisibility == TestVisibility.HIDE_EVERYTHING)) {
            return false
        }

        return true
    }

    val cssLabel: String
        get() {
            return when (reportValue) {
                "OK" -> "label-success"
                "NOK" -> "label-danger"
                "Not Enough Tests" -> "label-warning"
                else -> ""
            }
        }
}
