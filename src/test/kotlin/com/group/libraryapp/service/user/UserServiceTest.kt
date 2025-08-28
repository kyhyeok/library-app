package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserUpdateRequest
import org.assertj.core.api.AssertionsForClassTypes
import org.assertj.core.api.AssertionsForInterfaceTypes
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserServiceTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val userService: UserService,
    service: UserService
) {

    @AfterEach
    fun clean() {
        userRepository.deleteAll()
    }

    @Test
    @DisplayName("유저 저장이 정상 동작한다")
    fun saveUser() {
        // given
        val request = UserCreateRequest("혁", null);

        // when
        userService.saveUser(request);

        // then
        val results = userRepository.findAll()
        AssertionsForInterfaceTypes.assertThat(results).hasSize(1)
        AssertionsForClassTypes.assertThat(results[0].name).isEqualTo(request.name)
        AssertionsForClassTypes.assertThat(results[0].age).isNull()
    }

    @Test
    @DisplayName("유저 조회가 정상 동작한다")
    fun getUsers() {
        // given
        userRepository.saveAll(listOf(
            User("A", 100),
            User("B", null),
        ))

        // when
        val results = userService.getUsers()

        // then
        AssertionsForInterfaceTypes.assertThat(results).hasSize(2) // [UserResponse(), UserResponse()]
        AssertionsForInterfaceTypes.assertThat(results).extracting("name") // ["A", "B"]
            .containsExactlyInAnyOrder("A", "B")
        AssertionsForInterfaceTypes.assertThat(results).extracting("age") // [100, null]
            .containsExactlyInAnyOrder(100, null)
    }

    @Test
    @DisplayName("유저 수정이 정상 동작한다")
    fun updateUser() {
        // given
        val savedUser = userRepository.save(User("A", null))
        val request = UserUpdateRequest(savedUser.id, "B")

        // when
        userService.updateUserName(request)

        // then
        val result = userRepository.findAll()[0]

        AssertionsForClassTypes.assertThat(result.name).isEqualTo(request.name)
    }

    @Test
    @DisplayName("유저 삭제 정상 동작한다")
    fun deleteUser() {
        // given
        userRepository.save(User("A", null))

        // when
        userService.deleteUser("A")

        // then
        AssertionsForInterfaceTypes.assertThat(userRepository.findAll()).isEmpty()
    }
}