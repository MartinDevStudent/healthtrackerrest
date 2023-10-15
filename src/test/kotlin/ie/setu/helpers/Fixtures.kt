package ie.setu.helpers

import ie.setu.domain.Activity
import ie.setu.domain.User
import org.joda.time.DateTime

val nonExistingEmail = "112233445566778testUser@xxxxx.xx"
val validName = "Test User 1"
val validEmail = "testuser1@test.com"

val users = arrayListOf<User>(
    User(name = "Alice Wonderland", email = "alice@wonderland.com", id = 1, level = "user", passwordHash = "111111111"),
    User(name = "Bob Cat", email = "bob@cat.ie", id = 2, level = "user", passwordHash = "111111111"),
    User(name = "Mary Contrary", email = "mary@contrary.com", id = 3, level = "user", passwordHash = "111111111"),
    User(name = "Carol Singer", email = "carol@singer.com", id = 4, level = "user", passwordHash = "111111111")
)

val activities = arrayListOf<Activity>(
    Activity(description = "Running", duration = 12.5, calories = 112, started = DateTime("2020-06-10T05:59:27.258Z"), id = 1, userId = 2),
    Activity(description = "Dancing", duration = 30.0, calories = 250, started = DateTime("2020-06-12T05:59:27.258Z"), id = 2, userId = 1),
    Activity(description = "Cycling", duration = 45.0, calories = 300, started = DateTime("2020-06-18T05:59:27.258Z"), id = 3, userId = 2),
    Activity(description = "Swimming", duration = 25.0, calories = 275, started = DateTime("2020-06-26T05:59:27.258Z"), id = 4, userId = 2),
)