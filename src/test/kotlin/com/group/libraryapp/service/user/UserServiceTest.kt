package com.group.libraryapp.service.user

import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanStatus
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserUpdateRequest
import org.assertj.core.api.AssertionsForClassTypes
import org.assertj.core.api.AssertionsForInterfaceTypes
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserServiceTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val userLoanHistoryRepository: UserLoanHistoryRepository,
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
        val request = UserUpdateRequest(savedUser.id!!, "B")

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

    @Test
    @DisplayName("대출 기록이 없는 유저도 응답에 포함된다")
    fun getUserLoanHistoriesNoBookLean() {
        // given
        userRepository.save(User("A", null))

        // when
        val results = userService.getUserLoanHistories()

        // then
        assertThat(results).hasSize(1)
        assertThat(results[0].name).isEqualTo("A")
        assertThat(results[0].books).isEmpty()
    }

    @Test
    @DisplayName("대출 기록이 많은 유저도 응답이 정상 동작한다")
    fun getUserLoanHistoriesLotsBooksLean() {
        // given
        val savedUser = userRepository.save(User("A", null))
        userLoanHistoryRepository.saveAll(listOf(
            UserLoanHistory.fixture(savedUser, "책 leaned 1", UserLoanStatus.LOANED),
            UserLoanHistory.fixture(savedUser, "책 leaned 2", UserLoanStatus.LOANED),
            UserLoanHistory.fixture(savedUser, "책 returned 3", UserLoanStatus.RETURNED),
        ))

        // when
        val results = userService.getUserLoanHistories()

        // then
        assertThat(results).hasSize(1)
        assertThat(results[0].name).isEqualTo("A")
        assertThat(results[0].books).hasSize(3)
        assertThat(results[0].books).extracting("name")
            .containsExactlyInAnyOrder("책 leaned 1", "책 leaned 2", "책 returned 3")
        assertThat(results[0].books).extracting("isReturn")
            .containsExactlyInAnyOrder(false, false, true)
    }

    @Test
    @DisplayName("대출 기록이 많은 유저도 응답이 정상 동작한다 + 대출 기록이 많은 유저도 응답이 정상 동작한다")
    fun getUserLoanHistoriesBoth() {
        // given
        val savedUsers = userRepository.saveAll(listOf(
            User("A", null),
            User("B", null),
        ))
        userLoanHistoryRepository.saveAll(listOf(
            UserLoanHistory.fixture(savedUsers[0], "책 leaned 1", UserLoanStatus.LOANED),
            UserLoanHistory.fixture(savedUsers[0], "책 leaned 2", UserLoanStatus.LOANED),
            UserLoanHistory.fixture(savedUsers[0], "책 returned 3", UserLoanStatus.RETURNED),
        ))

        // when
        val results = userService.getUserLoanHistories()

        // then
        assertThat(results).hasSize(2)

        val userAResult = results.first { it.name == "A" }

        assertThat(userAResult.books).hasSize(3)
        assertThat(userAResult.books).extracting("name")
            .containsExactlyInAnyOrder("책 leaned 1", "책 leaned 2", "책 returned 3")
        assertThat(userAResult.books).extracting("isReturn")
            .containsExactlyInAnyOrder(false, false, true)

        val userBResult = results.first { it.name == "B" }
        assertThat(userBResult.books).isEmpty()
    }
}
