package com.ivy.math

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ivy.parser.Parser
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class ExpressionParserTest {

    private lateinit var parser: Parser<TreeNode>

    @BeforeEach
    fun setup() {
        parser = expressionParser()
    }

    @ParameterizedTest
    @CsvSource(
        "9*(3+6),81.0",
        "3+6/3-(-10),15.0",
        "5+5,10.0",
        "5*5,25.0",
        "5-5,0.0",
        "100/(4*5),5.0",
        "5.000000000,5.0"
    )
    fun `Test evaluating expression`(expression: String, expected: Double) {
        val result = parser(expression).first()

        val actual = result.value.eval()

        assertThat(actual).isEqualTo(expected)
    }
}