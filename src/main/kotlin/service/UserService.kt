package service

import model.User

class UserService {
    // In-memory user storage for simplicity
    // Future task could be loading up a small db container, would be fun!
    private val users = mutableListOf(
        User(
            id = "1",
            username = "gandalf_the_grey",
            email = "gandalf@grey.com",
            name = "Gandalf",
            password = "M@g1c"
        ),
        User(
            id = "2",
            username = "gandalf_the_white",
            email = "gandalf@white.com",
            name = "Gandalf",
            password = "M@g1c_"
        )
    )

    fun validateUser(username: String, password: String): User? {
        return users.find { it.username == username && it.password == password }
    }

    fun findUserById(id: String): User? {
        return users.find { it.id == id }
    }

    fun findUserByUsername(username: String): User? {
        return users.find { it.username == username }
    }
}