package com.ivy.core.ui.time.picker.date

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ivy.common.androidtest.IvyAndroidTest
import com.ivy.core.ui.time.picker.date.data.PickerDay
import com.ivy.core.ui.time.picker.date.data.PickerMonth
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
class DatePickerViewModelTest : IvyAndroidTest() {

    private lateinit var viewModel: DatePickerViewModel

    override fun setup() {
        super.setup()
        viewModel = DatePickerViewModel(context, timeProvider)
    }

    @Test
    fun testSelectingDate() = runTest {

        // Making sure, the test runs with a month != February, so
        // February can be selected
        setDate(LocalDate.of(2023, 1, 1))
        viewModel.uiState.test {
            awaitItem() // Ignore initial emission

            viewModel.onEvent(DatePickerEvent.DayChange(PickerDay("30", 30)))
            awaitItem() // Skip day emission

            viewModel.onEvent(DatePickerEvent.MonthChange(PickerMonth("Feb", 2)))


            val finalEmission = awaitItem()

            val timeProviderDate = timeProvider.dateNow()
            assertThat(finalEmission.selected).isEqualTo(
                LocalDate.of(timeProviderDate.year, 2, 28)
            )

            // This is my solution which is also correct but many assertions :)
            // We forgot to manipulate time to make it predictable (avoids flaky tests) which is resolved by
            // creating TimeProviderFake and injecting it in the parent class
            // and calling setDate at the first line of this function.
//            val em1 = awaitItem()
//
//            var expectedSelectedDate = LocalDate.of(2023, 1, 30)
//
//            assertThat(em1.selected).isEqualTo(expectedSelectedDate)
//            assertThat(em1.daysListSize).isEqualTo(31)
//
//            viewModel.onEvent(DatePickerEvent.MonthChange(PickerMonth("Feb", 2)))
//
//            val em2 = awaitItem()
//
//            expectedSelectedDate = LocalDate.of(2023, 2, 28)
//
//            assertThat(em2.selected).isEqualTo(expectedSelectedDate)
//            assertThat(em2.daysListSize).isEqualTo(29)
        }
    }
}