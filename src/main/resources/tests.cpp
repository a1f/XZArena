#include "prelude.h"
#include "main.h"
#include "rand.h"
#include "test_util.h"

Random rnd;
const int TL = 2000;
// const int TL = $$TL$$;

void runSolution(const tuple<$$INPUT_AND_OUTPUT$$>& data) {
    $$PARAM_NAMES_INIT$$

    boost::execution_monitor mon;
    mon.p_timeout.set(TL);
    $$OUTPUT_TYPE$$ result;
    mon.vexecute(boost::function<void()>([&]() {
        result = $$CLASS_NAME$$().$$METHOD_NAME$$($$PARAM_NAMES$$);
    }));
    $$EXPECT_REGION$$
}

$$SAMPLE_AREA$$

/*
namespace stress_test {
    const int ITERS = 1;

    $$OUTPUT_TYPE$$ $$METHOD_NAME$$($$INPUT$$) {
    }

    tuple<$$INPUT_AND_OUTPUT$$> getInput() {
        $$PARAM_NAMES_INIT$$
        auto expected = $$METHOD_NAME$$($$PARAM_NAMES$$);
        return make_tuple($$PARAM_NAMES$$, expected);
    }
}

TEST(_tc_$$LETTER$$, stress) {
    FOR(iter, stress_test::ITERS) {
        ASSERT_NO_FATAL_FAILURE(runSolution(stress_test::getInput()));
    }
}
*/


