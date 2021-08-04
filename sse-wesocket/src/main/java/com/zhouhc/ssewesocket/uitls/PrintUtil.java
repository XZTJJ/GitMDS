package com.zhouhc.ssewesocket.uitls;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 文本打印工具,制作成表格的样子
 */
public class PrintUtil {


    /**
     * 要打印的内容
     *
     * @param prints 数据内容(如果有标题,标题放第一行)
     */
    public static String printForTable(String[][] prints) {
        return printForTable(prints, 130);
    }

    /**
     * 要打印的内容
     *
     * @param prints 数据内容(如果有标题,标题放第一行)
     * @param width  表格宽度
     */
    public static String printForTable(String[][] prints, int width) {
        int[] suitableLength = getSuitableLength(prints, width);

        int row = prints.length, column = prints[0].length;
        //打印
        final StringBuilder line = new StringBuilder("+");
        Arrays.stream(suitableLength).forEach(max -> line.append(String.format("%-" + max + "s", "-")).append("+"));
        line.append("%n");
        String lineStrTest = String.format(line.toString(), "").replaceAll(" ", "-");

        StringBuilder csb = new StringBuilder();
        //开始打印，遍历所有的行
        for (int i = 0; i < row; i++) {
            csb.append(lineStrTest);
            //列序号
            int cloumnIndex = 0;
            boolean[] columnEnd = new boolean[column];
            Arrays.fill(columnEnd, false);
            //确保整行都完整了
            while (isNotEnd(columnEnd) || cloumnIndex != 0) {
                csb.append("|");
                //列宽,字符串,其他标记
                int cloumnWidth = suitableLength[cloumnIndex];
                String temp = prints[i][cloumnIndex] == null ? "" : prints[i][cloumnIndex];
                int tempIndex = 0, tempCount = 0, chineseNum = 0;
                //第一个大于 cloumnWidth 的下标
                while (tempCount < cloumnWidth && tempIndex < temp.length()) {
                    if (isChinese(temp.charAt(tempIndex))) {
                        tempCount += 2;
                        chineseNum += 1;
                    } else {
                        tempCount += 1;
                    }
                    tempIndex += 1;
                }
                //处理超过的  cloumnWidth 的问题 和 越界处理,指针都要回退
                if (tempIndex == temp.length() || tempCount >= cloumnWidth)
                    tempIndex -= 1;
                csb.append(String.format("%-" + (cloumnWidth - chineseNum) + "s", temp.substring(0, tempIndex + 1)));
                //该列是否处理完了
                if (tempIndex == temp.length() - 1) {
                    prints[i][cloumnIndex] = "";
                    columnEnd[cloumnIndex] = true;
                } else {
                    prints[i][cloumnIndex] = temp.substring(tempIndex);
                }
                //表示到了最后一列了，需要换行
                if (cloumnIndex == column - 1) {
                    csb.append("|%n");
                }
                //下一列
                cloumnIndex = cloumnIndex + 1 == column ? 0 : cloumnIndex + 1;
            }
        }
        csb.append(lineStrTest);
        String result = String.format(csb.toString(), "");
        return result;
    }

    //找到每一列最适合的长度
    private static int[] getSuitableLength(String[][] prints, int width) {
        int row = prints.length, column = prints[0].length;
        //每列最大长度
        int[] maxLength = new int[column];
        for (int i = 0; i < row; i++)
            for (int j = 0; j < column; j++)
                maxLength[j] = Math.max(maxLength[j], getStrLength(prints[i][j]));

        int sum = Arrays.stream(maxLength).sum();
        //没有超过指定长度,直接返回
        if (width >= sum)
            return maxLength;
        //自动适应长度
        final int averageCloumn = width / column;
        int lessAverageColumnSum = Arrays.stream(maxLength).filter(max -> max <= averageCloumn).sum();
        int lessAverageColumnCount = (int) Arrays.stream(maxLength).filter(max -> max <= averageCloumn).count();
        final int moreAverageColumn = (width - lessAverageColumnSum) / (column - lessAverageColumnCount);
        maxLength = Arrays.stream(maxLength).map(max -> max <= averageCloumn ? max : moreAverageColumn).toArray();
        return maxLength;
    }

    /**
     * 汉字的编码在 19968 和 40869之间
     *
     * @param c 字符
     * @return
     */
    public static boolean isChinese(char c) {
        int number = (int) c;
        if (19968 <= number && number < 40869)
            return true;
        return false;
    }

    /**
     * 字符串最大长度,一个中文占两个字符
     */
    private static int getStrLength(String args) {
        if (args == null || args.length() == 0)
            return 0;
        int temp = Arrays.stream(args.split("")).map(c -> (isChinese(c.charAt(0)) ? 2 : 1)).collect(Collectors.summingInt(max -> max));
        return temp;
    }

    /**
     * 是否都处理完成的标志
     */
    private static boolean isNotEnd(boolean[] isEndArrays) {
        for (boolean s : isEndArrays)
            if (!s)
                return true;
        return false;
    }

}
