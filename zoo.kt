import java.util.*


fun main() {
    var zoo = Zoo()
    val cat = Cat(100.0)
    zoo = Zoo(listOf(cat, Dog(1.2), Hippo(1.75), Horse(2.34), Fish(0.2)))
    val cats = zoo.animalsIsInstance<Cat>()
    zoo.removeAnimal(cat.id)
}

class Zoo(
    val animals: TreeSet<Animal> = TreeSet(compareBy<Animal> { it.height }),
    val animalIds: HashMap<UUID, Animal> = HashMap(),
    private val zookeepers: HashMap<UUID, Zookeeper> = HashMap(),
    private val zookeepersByName: HashMap<String, MutableList<Zookeeper>> = HashMap(),
    private val assignZookeeperToAnimal: HashMap<Animal, Zookeeper> = HashMap(),
    private val assignAnimalsToZookeeper: HashMap<Zookeeper, MutableList<Animal>> = HashMap(),
    private val talkingAnimals: MutableList<Talking> = mutableListOf()
) {
     private lateinit var firstZookeeper: Zookeeper

    constructor(collection: Collection<Animal>) : this() {
        animals.addAll(collection)
        hireFirstZookeeper()
        assignAnimalsToZookeeper.getValue(firstZookeeper).addAll(animals)
        for (animal in collection) {
            assignZookeeperToAnimal[animal] = firstZookeeper
        }
    }

    fun hireFirstZookeeper() {
        firstZookeeper = Zookeeper("Zack")
        zookeepers[firstZookeeper.id] = firstZookeeper
        zookeepersByName[firstZookeeper.name] = mutableListOf(firstZookeeper)
        assignAnimalsToZookeeper[firstZookeeper] = mutableListOf()
    }

    fun addAnimal(animal: Animal) {
        if (!this::firstZookeeper.isInitialized) {
            hireFirstZookeeper()
        }
        animals.add(animal)
        assignZookeeperToAnimal[animal] = firstZookeeper
        assignAnimalsToZookeeper.getValue(firstZookeeper).add(animal)
        if (animal is Talking) {
            talkingAnimals.add(animal)
        }
    }

    fun findAnimal(id: UUID): Animal? {
        return animalIds[id]
    }

    fun removeAnimal(id: UUID) {
        val animal = findAnimal(id) ?: return
        animals.remove(animal)
        val assignedZookeeper = assignZookeeperToAnimal.getValue(animal)
        assignAnimalsToZookeeper.getValue(assignedZookeeper).remove(animal)
        assignZookeeperToAnimal.remove(animal)
        animalIds.remove(id)
        if (animal is Talking) {
            talkingAnimals.remove(animal)
        }
    }

    fun assign(animal: Animal, zookeeper: Zookeeper) {
        if (!animals.contains(animal)) {
            this.addAnimal(animal)
        }
        assignZookeeperToAnimal[animal] = zookeeper
        if (assignAnimalsToZookeeper[zookeeper] == null)
            assignAnimalsToZookeeper[zookeeper] = mutableListOf(animal)
        else
            assignAnimalsToZookeeper.getValue(zookeeper).add(animal)
    }

    fun assignById(animal: Animal, zookeeperId: UUID) {
        if (!animals.contains(animal)) {
            this.addAnimal(animal)
        }
        val zookeeper = zookeepers[zookeeperId] ?: throw IllegalArgumentException("No zookeeper with id: $zookeeperId")
        assignZookeeperToAnimal[animal] = zookeeper
        if (assignAnimalsToZookeeper[zookeeper] == null)
            assignAnimalsToZookeeper[zookeeper] = mutableListOf(animal)
        else
            assignAnimalsToZookeeper.getValue(zookeeper).add(animal)
    }

    fun findByZookeeper(zookeeperId: UUID): Collection<Animal> {
        val zookeeper = zookeepers[zookeeperId] ?: throw IllegalArgumentException("No zookeeper with id: $zookeeperId")
        return assignAnimalsToZookeeper[zookeeper] ?: emptyList()
    }

    fun findByZookeeper(zookeeperName: String): Collection<Animal> {
        val zookeepers = zookeepersByName[zookeeperName] ?: return emptyList()
        val list: MutableList<Animal> = mutableListOf()
        for (zookeeper in zookeepers) {
            list.addAll(assignAnimalsToZookeeper[zookeeper] ?: emptyList())
        }
        return list
    }

    fun animalsHigherThen(height: Double): Collection<Animal>? {
        return animals.tailSet(object: Animal(UUID.randomUUID(), height, "none"){})
    }

    fun animalsTalking(): Collection<Animal> {
        return talkingAnimals
    }

    inline fun <reified T: Animal> animalsIsInstance(): Collection<Animal> {
        return animals.filterIsInstance<T>()
    }
}

abstract class Animal(
    open val id: UUID = UUID.randomUUID(),
    open val height: Double,
    open val species: String
)

abstract class Talking(
    override val height: Double,
    override val species: String,
    open val says: String
): Animal(height = height, species = species)

class Cat(
    override val height: Double
): Talking(height = height, species = "cat", says = "Meow")

class Dog(
    override val height: Double
): Talking(height = height, species = "dog", says = "Woof")

class Hippo(
    override val height: Double
): Talking(height = height, species = "hippo", says = "Good Morning")

class Horse(
    override val height: Double
): Talking(height = height, species = "horse", says = "Neigh")

class Fish(
    override val height: Double
): Animal(height = height, species = "fish")

class Zookeeper(
    val name: String,
    val id: UUID = UUID.randomUUID()
)