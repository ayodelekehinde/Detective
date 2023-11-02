package io.github.detective.dectector

object Categorizer {

    val animalNames = listOf(
        "dog", "cat", "elephant", "lion", "tiger", "giraffe", "bear", "panda", "dolphin", "penguin", "kangaroo",
        "koala", "zebra", "horse", "cheetah", "gorilla", "fox", "rabbit", "squirrel", "camel", "wolf", "hippopotamus", "chimpanzee", "orangutan", "octopus", "owl", "eagle", "penguin", "crocodile", "alligator"
    ).toSet()
    val things = listOf(
        "table", "chair", "tv", "phone", "bag", "car", "computer", "phone", "book", "lamp", "pen", "guitar", "shoes",
        "television", "backpack", "keyboard", "watch", "bike", "camera", "glasses", "wallet", "remote", "umbrella",
        "basket", "mirror", "scissors", "plate", "shovel", "gloves", "hat", "sunglasses", "hanger", "calculator", "spoon"
    ).toSet()
    val plantNames = listOf(
        "rose", "tulip", "daisy", "sunflower", "cactus", "orchid", "bamboo", "fern", "ivy", "palm", "maple", "oak", "pine", "acacia", "eucalyptus",
        "lavender", "jasmine", "lily", "daffodil", "violet", "carnation", "hibiscus", "tulip", "moss", "thyme", "sage", "rosemary", "basil", "mint",
    ).toSet()

    fun categorize(label: String): Category{
       return when{
            animalNames.contains(label.lowercase()) -> Category.Animal
           things.contains(label.lowercase()) -> Category.Things
           plantNames.contains(label.lowercase()) -> Category.Plants
           label.lowercase() == "person" -> Category.Person
           else -> Category.Unknown
        }
    }
}

enum class Category{
    Animal,Things,Plants,Person,Unknown
}