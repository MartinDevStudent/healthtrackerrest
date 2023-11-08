package ie.setu.helpers

import ie.setu.domain.Activity
import ie.setu.domain.Ingredient
import ie.setu.domain.Meal
import ie.setu.domain.RecommendedDailyAllowance
import ie.setu.domain.user.User
import org.joda.time.DateTime

const val NONE_EXISTING_EMAIL = "112233445566778testUser@xxxxx.xx"
const val NON_EXISTING_MEAL_ID = -999
const val INCORRECT_PASSWORD = "**********"
const val INVALID_MEAL_NAME = "football"

const val VALID_NAME = "Test User 1"
const val VALID_EMAIL = "testuser1@test.com"
const val VALID_PASSWORD = "password"
var validDescription = "Test Description"
var validDuration = 100.0
var validCalories = 150
var validStarted = DateTime("2020-06-10T05:59:27.258Z")
var validMealName = "caesar salad"

val users =
    arrayListOf(
        User(name = "Alice Wonderland", email = "alice@wonderland.com", id = 1, level = "user", passwordHash = "111111111"),
        User(name = "Bob Cat", email = "bob@cat.ie", id = 2, level = "user", passwordHash = "111111111"),
        User(name = "Mary Contrary", email = "mary@contrary.com", id = 3, level = "user", passwordHash = "111111111"),
        User(name = "Carol Singer", email = "carol@singer.com", id = 4, level = "user", passwordHash = "111111111"),
    )

val activities =
    arrayListOf(
        Activity(
            description = "Running",
            duration = 12.5,
            calories = 112,
            started = DateTime("2020-06-10T05:59:27.258Z"),
            id = 1,
            userId = 2,
        ),
        Activity(
            description = "Dancing",
            duration = 30.0,
            calories = 250,
            started = DateTime("2020-06-12T05:59:27.258Z"),
            id = 2,
            userId = 1,
        ),
        Activity(
            description = "Cycling",
            duration = 45.0,
            calories = 300,
            started = DateTime("2020-06-18T05:59:27.258Z"),
            id = 3,
            userId = 2,
        ),
        Activity(
            description = "Swimming",
            duration = 25.0,
            calories = 275,
            started = DateTime("2020-06-26T05:59:27.258Z"),
            id = 4,
            userId = 2,
        ),
    )

val meals =
    arrayListOf(
        Meal(id = 1, name = "Burger"),
        Meal(id = 2, name = "Fries"),
        Meal(id = 3, name = "Salmon Salad"),
        Meal(id = 4, name = "Lamb"),
    )

val ingredients =
    arrayListOf(
        Ingredient(
            id = 1,
            name = "Burger",
            calories = 237.7,
            servingSizeG = 100.0,
            fatTotalG = 11.5,
            fatSaturatedG = 4.7,
            proteinG = 15.2,
            sodiumMg = 356,
            potassiumMg = 137,
            cholesterolMg = 53,
            carbohydratesTotalG = 18.1,
            fiberG = 0.0,
            sugarG = 0.0,
        ),
        Ingredient(
            id = 2,
            name = "Fries",
            calories = 317.7,
            servingSizeG = 100.0,
            fatTotalG = 14.8,
            fatSaturatedG = 2.3,
            proteinG = 3.4,
            sodiumMg = 212,
            potassiumMg = 124,
            cholesterolMg = 0,
            carbohydratesTotalG = 41.1,
            fiberG = 3.8,
            sugarG = 0.3,
        ),
        Ingredient(
            id = 3,
            name = "Salmon Salad",
            calories = 97.8,
            servingSizeG = 100.0,
            fatTotalG = 5.2,
            fatSaturatedG = 0.9,
            proteinG = 10.3,
            sodiumMg = 287,
            potassiumMg = 179,
            cholesterolMg = 28,
            carbohydratesTotalG = 1.8,
            fiberG = 0.9,
            sugarG = 0.8,
        ),
        Ingredient(
            id = 4,
            name = "Lamb",
            calories = 301.2,
            servingSizeG = 100.0,
            fatTotalG = 20.5,
            fatSaturatedG = 8.8,
            proteinG = 24.4,
            sodiumMg = 71,
            potassiumMg = 187,
            cholesterolMg = 98,
            carbohydratesTotalG = 0.0,
            fiberG = 0.0,
            sugarG = 0.0,
        ),
    )

val recommendedDailyAllowance =
    RecommendedDailyAllowance(
        calories = 2500.0,
        fatTotalG = 78.0,
        fatSaturatedG = 22.0,
        proteinG = 60.0,
        sodiumMg = 2300,
        potassiumMg = 2500,
        cholesterolMg = 300,
        carbohydratesTotalG = 130.0,
        fiberG = 38.0,
        sugarG = 36.0,
    )
