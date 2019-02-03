import com.topcoder.client.contestant.ProblemComponentModel;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.TestCase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

class CodeGenerator {
    final static String path = "/Users/alf/acm/alf/_/";
    final static String PLUGIN_PATH = "/Users/alf/dev/TCPlugin/resources/";

    static void generate(ProblemComponentModel problemComponentModel) {
        MyLogger.getInstance().log("CodeGenerator generate");
        final String[] paramLists = prepareParameterList(
                problemComponentModel.getParamNames(),
                problemComponentModel.getParamTypes());

        final String paramList = paramLists[0];
        final String input = paramLists[1];
        MyLogger.getInstance().log("param list " + paramList);
        MyLogger.getInstance().log("input " + input);

        final String returnType = prepareType(problemComponentModel.getReturnType());
        final String inputAndOutput = input + ", " + returnType;

        MyLogger.getInstance().log("returnType " + returnType);
        MyLogger.getInstance().log("inputAndOutput " + inputAndOutput);

        final String solutionTemplate = generateSolution(
                problemComponentModel.getClassName(),
                problemComponentModel.getMethodName(),
                returnType,
                paramList
        );
        final String problemLetter = getProblemLetter(problemComponentModel.getPoints().intValue());
        final int timeLimit = 0;//problemComponentModel.getComponent().getProblemCustomSettings().getExecutionTimeLimit();

        MyLogger.getInstance().log("problemLetter " + problemLetter);
        MyLogger.getInstance().log("timeLimit " + timeLimit);
        MyLogger.getInstance().log("solution btw");
        MyLogger.getInstance().log(solutionTemplate);

        writeMainH(problemLetter, solutionTemplate);

        final String samples = prepareSamples(
                problemLetter,
                problemComponentModel.getReturnType(),
                problemComponentModel.getParamTypes(),
                problemComponentModel.getTestCases());

        final String paramNamesInit = prepareParamList(
                problemComponentModel.getParamNames(),
                problemComponentModel.getParamTypes(),
                problemComponentModel.getReturnType());

        final String paramNames = prepareParamNames(problemComponentModel.getParamNames());
        final String expectedCompare = prepareExpect(isSimpleType(problemComponentModel.getReturnType()));
        String testFile = prepareTestFile(
                problemLetter, samples, String.valueOf(timeLimit), paramList,
                paramNamesInit, returnType, paramNames, inputAndOutput, problemComponentModel.getClassName(),
                problemComponentModel.getMethodName(), expectedCompare);
        writeTest(problemLetter, testFile);
    }

    private static String prepareParamNames(String[] paramNames) {
        if (paramNames.length == 0) {
            return "";
        }
        StringBuilder res = new StringBuilder("");
        for (String s : paramNames) {
            res.append(s).append(", ");
        }
        return res.substring(0, res.length() - 2);
    }

    static String prepareParamList(String[] paramName, DataType[] paramType, DataType returnType) {
        StringBuilder b = new StringBuilder("");
        for (int i = 0; i < paramName.length; ++i) {
            String type = prepareType(paramType[i]);
            b.append(type).append(" ").append(paramName[i]).append(" = get<").append(i).append(">(data);").append("\n");
        }
        String type = prepareType(returnType);
        b.append(type).append(" expected = get<").append(paramName.length).append(">(data);").append("\n");
        return b.toString();
    }

    static String prepareExpect(boolean simpleResult) {
        if (simpleResult) {
            return "EXPECT_EQ(result, expected);";
        } else {
            return "EXPECT_THAT(result, ::testing::ContainerEq(expected));";
        }
    }

    static String prepareTestFile(String problemLetter, String samples, String timeLimit, String input,
                                  String paramNamesInit, String outType, String paramNames, String inputAndOutput,
                                  String className, String methodName, String expected) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileInputStream(PLUGIN_PATH + "code_template/tests.cpp"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        TreeMap<String, String> regions = new TreeMap<String, String>();
        regions.put("$$TL$$", timeLimit);
        regions.put("$$INPUT$$", input);
        regions.put("$$PARAM_NAMES_INIT$$", paramNamesInit);
        regions.put("$$OUTPUT_TYPE$$", outType);
        regions.put("$$PARAM_NAMES$$", paramNames);
        regions.put("$$SAMPLE_AREA$$", samples);
        regions.put("$$LETTER$$", problemLetter);
        regions.put("$$INPUT_AND_OUTPUT$$", inputAndOutput);
        regions.put("$$CLASS_NAME$$", className);
        regions.put("$$METHOD_NAME$$", methodName);
        regions.put("$$EXPECT_REGION$$", expected);
        StringBuilder result = new StringBuilder("");
        while (true) {
            try {
                String line = scanner.nextLine();
                MyLogger.getInstance().log("Line before: " + line);
                line = replaceRegions(line, regions);
                MyLogger.getInstance().log("Line after: " + line);
                result.append(line).append("\n");
            } catch (Exception e) {
                break;
            }
        }
        return result.toString();
    }

    static void writeTest(String problemLetter, String testContent) {
        try {
            PrintWriter out = new PrintWriter(path + "tc_" + problemLetter + "/tests.cpp");
            out.println(testContent);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static String replaceRegions(String line, TreeMap<String, String> regions) {
        for (String region : regions.keySet()) {
            if (line.contains(region)) {
                line = line.replace(region, regions.get(region));
            }
        }
        return line;
    }

    static String prepareSamples(String problemLetter, DataType returnType, DataType[] paramTypes, TestCase[] testCases) {
        StringBuilder result = new StringBuilder("");
        for (int i = 0; i < testCases.length; ++i) {
            String[] testIO = getTestInfo(testCases[i], paramTypes, returnType);
            result.append("\n");
            result.append("TEST(_tc_").append(problemLetter).append(", sample").append(i + 1).append(") {").append("\n");
            result.append("    runSolution(make_tuple(").append(testIO[0]).append(", ").append(testIO[1]).append("));").append("\n");
            result.append("}").append("\n");
            result.append("\n");
        }
        return result.toString();
    }

    static String[] getTestInfo(TestCase testCase, DataType[] paramTypes, DataType returnType) {
        String[] params = new String[]{};//testCase.getInput();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < params.length; ++i) {
            builder.append(getTestValue(params[i], paramTypes[i])).append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        return new String[]{builder.toString(), getTestValue(/*testCase.getOutput()*/"", returnType)};
    }

    static String getTestValue(String paramValue, DataType dataType) {
        if (isSimpleType(dataType)) {
            return paramValue;
        } else {
            String type = prepareType(dataType);
            return type + paramValue;
        }
//        if (dataType.getBaseName().toLowerCase().equals("string")) {
//            if (isSimpleType(dataType)) {
//                return "\"" + paramValue + "\"";
//            } else {
//                return recursiveStringFix(paramValue, dataType.getDimension());
//            }
//        } else {
//            return paramValue;
//        }
    }

    private static String recursiveStringFix(String paramValue, int dimension) {
        if (dimension == 0) {
            return "\"" + paramValue + "\"";
        }
        String insideDimention = paramValue.trim().substring(1, paramValue.length() - 1).trim();
        String[] tokens = insideDimention.split(",");
        List<String> eachToken = new ArrayList<String>();
        for (String token : tokens) {
            eachToken.add(recursiveStringFix(token.trim(), dimension - 1));
        }
        StringBuilder result = new StringBuilder("");
        for (String token : eachToken) {
            result.append(token).append(", ");
        }
        return "{" + result.substring(0, result.length() - 2) + "}";
    }

    static void writeMainH(final String letter, final String solution) {
        try {
            MyLogger.getInstance().log(path + "tc_" + letter + "/main.h");
            PrintWriter writer = new PrintWriter(path + "tc_" + letter + "/main.h");
            writer.println("#pragma once");
            writer.println("#include \"prelude.h\"");
            writer.println();
            writer.println(solution);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getProblemLetter(int score) {
        if (score > 650) {
            return "c";
        } else if (score < 400) {
            return "a";
        }
        return "b";
    }

    static String[] prepareParameterList(String[] names, DataType[] dataTypes) {
        StringBuilder builder = new StringBuilder("");
        StringBuilder simpleInputParams = new StringBuilder("");
        for (int i = 0; i < names.length; ++i) {
            String curType = prepareType(dataTypes[i]);
            simpleInputParams.append(curType);
            if (dataTypes[i].getDimension() != 0) {
                curType = "const " + curType + "& ";
            } else {
                curType = curType + " ";
            }
            curType += names[i];
            builder.append(curType);
            if (i < names.length - 1) {
                builder.append(", ");
                simpleInputParams.append(", ");
            }
        }
        return new String[]{builder.toString(), simpleInputParams.toString()};
    }

    static boolean isSimpleType(DataType dataType) {
        int dimentions = dataType.getDimension();
        return dimentions == 0;
    }

    static String prepareType(DataType dataType) {
        String typeName = dataType.getBaseName();
        int dimentions = dataType.getDimension();
        if ("String".equals(typeName)) {
            typeName = "string";
        }
        if ("long".equals(typeName)) {
            typeName = "int64";
        }
        final String prefix = "vector<";
        final String suffix = ">";
        if (dimentions == 0) {
            return typeName;
        }
        StringBuilder result = new StringBuilder("");
        for (int i = 0; i < dimentions; ++i) {
            result.append(prefix);
        }
        result.append(typeName);
        for (int i = 0; i < dimentions; ++i) {
            result.append(suffix);
        }
        return result.toString();
    }

    static String generateSolution(final String className,
                                   final String methodName,
                                   final String returnType,
                                   final String parameters) {
        return "class" + " " + className + " {" + "\n" +
                "public:" + "\n" +
                "// TC_REMOVE_BEGIN" + "\n" +
                "/// caide keep" + "\n" +
                "// TC_REMOVE_END" + "\n" +
                "    " + returnType + " " + methodName + "(" + parameters + ") {" + "\n" +
                "    " + "\n" +
                "    }" + "\n" +
                "};" + "\n";
    }

    static String generateSubmit(String problemName) {
        Scanner scanner;
        try {
            scanner = new Scanner(new FileInputStream(path + problemName + "/out.cpp"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean toIgnore = false;
        while (true) {
            try {
                String line = scanner.nextLine();
                if (line.contains("TC_REMOVE_BEGIN")) {
                    toIgnore = true;
                } else if (line.contains("TC_REMOVE_END")) {
                    toIgnore = false;
                    continue;
                }
                if (!toIgnore) {
                    result.append(line).append("\n");
                }
            } catch (Exception e) {
                break;
            }
        }
        return result.toString();
    }
}