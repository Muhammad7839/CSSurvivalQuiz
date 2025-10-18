package com.example.cssurvivalquiz.data

object Questions {
    val all = listOf(
        Question(
            id = 1,
            text = "What's the most common cause of a NullPointerException in Java?",
            options = listOf(
                "Using a reference before assigning it (object is null)",
                "Magic",
                "The compiler is broken",
                "Low battery"
            ),
            correct = setOf(0),
            multiple = false,
            fact = "NPE usually comes from dereferencing a null reference."
        ),
        Question(
            id = 2,
            text = "Which of these help you debug faster? (Select all that apply)",
            options = listOf(
                "Print statements (println/log)",
                "Use a debugger (breakpoints/step)",
                "Cry in the corner",
                "Rubber duck method (explain code out loud)"
            ),
            correct = setOf(0, 1, 3),
            multiple = true,
            fact = "Logs, a debugger, and explaining your code are simple and effective."
        ),
        Question(
            id = 3,
            text = "Off-by-one errors most often come from…",
            options = listOf(
                "Using <= where < was intended in a loop",
                "Forgetting to charge the laptop",
                "The CPU skipping a cycle",
                "Spaces vs tabs"
            ),
            correct = setOf(0),
            multiple = false,
            fact = "Loop bounds cause classic off-by-one mistakes."
        ),
        Question(
            id = 4,
            text = "In Kotlin/Android, 'Unresolved reference' usually means…",
            options = listOf(
                "A missing import or the symbol isn’t in scope",
                "Your app needs more RAM",
                "Gradle randomly deletes functions",
                "You must restart the phone"
            ),
            correct = setOf(0),
            multiple = false,
            fact = "Add the correct import or reference the right symbol."
        ),
        Question(
            id = 5,
            text = "Which practices prevent bugs before you even run the app? (Select all)",
            options = listOf(
                "Strong typing and compiler warnings",
                "Small unit tests",
                "Thoughtful code review",
                "Restarting the computer"
            ),
            correct = setOf(0, 1, 2),
            multiple = true,
            fact = "Types, tests, and reviews catch mistakes early."
        )
    )
}