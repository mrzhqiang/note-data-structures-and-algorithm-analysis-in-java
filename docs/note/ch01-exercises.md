# 第一章练习题

## 1.1

答案：

本仓库 [SelectionProblem.java](../../src/main/java/ch01/se01/SelectionProblem.java) 通过冒泡排序和截断法两种方式解决选择问题，同时它也是一个 JavaFX 的 GUI 程序，支持生成运行时间的表格。

最近一份运行时间表格，参见：[ch01-se01.md](../data/ch01-se01.md)。

## 1.2

答案：

本仓库 [Crossword.java](../../src/main/java/ch01/se01/Crossword.java) 通过有序四元组（行、列、方向、字符数）求解字谜游戏问题，同时也是一个 JavaFX 的 GUI 程序，能够显示如何找到字谜的位置，以及总共花费的时间。

## 1.3

思路：

我们知道一个 double 数字是这样的：`-520.1314`，它由符号、整数位、小数点、小数位四个部分组成。

1. 首先处理符号，利用 `double < 0` 这个判断决定是否输出 `-` 符号，同时用 `Math.abs` 方法去掉负数符号
2. 随后对整数部分进行处理，我们强制转换 `double` 为 `int`，屏蔽小数位，然后使用书中的例程 `printOut` 来打印整数部分
3. 接着打印小数点符号 `.`
4. 最后来处理小数位，通过限制小数位精度为 15，我们可以轻松判断递归的基本情况：当精度为 0 时，结束递归。而对小数位的处理，则是 `double * 10`，让离小数点最近的一位小数位升级为整数，再转为 `int` 丢给 `printOut` 去打印，同时用升级后的 `double` 减这个 `int` 得到全新的 `double`，同时和 `precision-1` 精度一起递归本身名为 `printDecimal` 的方法

**由于 `double` 减法的精度问题，我们使用 `BigDecimal` 来执行减法操作。**

答案：[PrintDouble.java](../../src/main/java/ch01/se03/PrintDouble.java)。

## 1.4

英文答案：

> The general way to do this is to write a procedure with heading
> 
> `void processFile( String fileName );`
> 
> which opens `fileName`, does whatever processing is needed, and then closes it. If a line of the form
> 
> `#include SomeFile`
> 
> is detected, then the call
> 
> `processFile( SomeFile );`
> 
> is made recursively. Self-referential includes can be detected by keeping a list of files for which a call to `processFile` has not yet terminated, and checking this list before making a new call to `processFile`.

思路：

1. 当我们拿到一个文件地址，首先判断它是否存在，以及是否为文件，如果是，继续下一步，否则忽略它
2. 读取文件的所有行内容，当遇到包含 `#include` 的一行时，获取其后的文件地址，递归调用自身
3. 当一行的内容不包含 `#include` 时，说明它是一个正常内容，直接打印出来即可

答案：[Include.java](../../src/main/java/ch01/se03/Include.java)

## 1.5

英文答案：

```java
public static int ones( int n )
{
    if( n < 2 )
        return n;
    return n % 2 + ones( n / 2 );
}
```

思考：

在正整数范围内，英文的答案没有问题，但遇到负整数的情况时，英文答案完全无效，从基本情况 `n < 2` 可以看出这一点。

那么，我们可以改进一下这个方法，以适应负整数的情况。

思路：

1. 我们知道在 Java 中，`int` 类型有 32 位长度，比如 `1` 大概是这样表示：`00000000 00000000 00000000 00000001`
2. 正整数转为负整数的话，先设置首位符号为 `1`，随后剩余的 31 位，取其正整数的反码，然后进行补码
3. 比如 `1` 转换为 `-1` 的过程如下：
  - 原码：`00000000000000000000000000000001`
  - 首位：`10000000000000000000000000000001`
  - 反码：`11111111111111111111111111111110`
  - 补码：`11111111111111111111111111111111`
4. 通过 `Interge.toBinaryString` 方法，我们也可以打印 `-1` 的二进制表示：`11111111111111111111111111111111`
5. 由此我们改进代码，先判断 `n < 0` 是否成立，若是，先将补码还原 `n = n + 1`，然后取绝对值 `n = Math.abs(n)`，再设一个反码标识 `i = 32`；否则不进行任何预处理
6. 最后，在执行完之前的逻辑后，当我们检测到 `i > 0` 时，则返回 `i - ones`，否则返回 `ones`，这便完成了负数的处理

改进的代码如下：
```java
private int ones(int n) {
    int i = 0;
    if (n < 0) {
        n = n + 1;
        n = Math.abs(n);
        i = 32;
    }
    int ones = n < 2 ? n : n % 2 + ones(n / 2);
    return i > 0 ? i - ones : ones;
}
```

**特别注意，`n = n + 1` 是对负数进行一次进位操作，这样可以消除补码的影响。**

答案：[BinaryOne.java](../../src/main/java/ch01/se03/BinaryOne.java)

## 1.6

思路：

1. `abc` 的所有排列是 `abc`,`acb`,`bac`,`bca`,`cab`,`cba` 共计六种
2. 从数量来看，呈阶乘分布：`3*2*1=6`，那么所有排列的数量公式为：`length!`
3. 从现象来看，每个字符排在首位的次数，恰好是剩余字符的数量，排在第二位的次数，又恰好是第二位之后的字符数量，所以可以采用递归来实现
4. 当 `low==high` 时，属于 `base case`，则可以打印字符排列
5. 通过 `from low to high` 进行循环调用，加上 `low+1`，满足不断推进法则
6. 我们发现，当前一个字符固定时，后面两个字符进行了位置交换，同时，当中间字符固定时，前后也进行了交换，所以交换位置是一个重要的信息，这就是我们的设计法则

核心代码：

```java
private void permute(String str) {
    char[] chars = str.toCharArray();
    permute(chars, 0, chars.length);
}

private void permute(char[] str, int low, int high) {
    // 1. base case
    if (low == high) {
        console.log(String.valueOf(str));
        return;
    }

    // 2. making progress
    for (int i = low; i < high; i++) {
        // 3. design rule
        permute(swap(str, low, i), low + 1, high);
    }
}

// 4. compound interest rule
private char[] swap(char[] str, int i1, int i2) {
    char[] dest = new char[str.length];
    System.arraycopy(str, 0, dest, 0, str.length);
    if (i1 != i2) {
        char temp = dest[i1];
        dest[i1] = dest[i2];
        dest[i2] = temp;
    }
    return dest;
}
```

答案：[Permute.java](../../src/main/java/ch01/se03/Permute.java)

## 1.7

英文答案：

> (a) The proof is by induction. The theorem is clearly true for 0 < X ≤ 1, since it is true for X = 1, and for X < 1, log X is negative. It is also easy to see that the theorem holds for 1 < X ≤ 2, since it is true for X = 2, and for X < 2, log X is at most 1. Suppose the theorem is true for p < X ≤ 2p(where p is a positive integer), and consider any 2p < Y ≤ 4p (p ≥ 1). Then log Y = 1+ log(Y/2) < 1+ Y/2 < Y/2 + Y/2 ≤ Y, where the first inequality follows by the inductive hypothesis.
>
> (b) Let 2^X = A. Then A^B = (2^X)^B = 2^(XB). Thus log A^B = XB. Since X = log A, the theorem is proved.

翻译：

```text
(a) 归纳法证明。
定理对所有的 `0 < X ≤ 1` 无疑是成立的，因为它对 `X = 1` 成立，并且对于 `X < 1`，`logX` 是负数。
显而易见的是，定理对 `1 < X ≤ 2` 成立，因为它对 `X = 2` 成立，并且对于 `X < 2`，`logX` 最接近 1。
假设定理对 `p < X ≤ 2p(p 为正整数)` 成立，并且考虑任意 `2p < Y ≤ 4p(p ≥ 1)`。
那么 `logY = 1 + log(Y/2) < 1 + Y/2 < Y/2 + Y/2 ≤ Y`，其中第一个不等式遵循归纳假设。

(b) 假设 `2^X = A`，那么 `A^B = (2^X)^B = 2^(XB)`，从而 `log(A^B)=XB`，因为 `X=logA`，定理得证。
```

解惑(a)：

题目是：`logX < X` 对所有的 `X > 0` 成立。

记住，`logX` 实际上是 `log₂X`。

1. 先看 `0 < X ≤ 1` 的情况，当 `X = 1` 时，`logX = 0 < X`，当 `0 < X < 1` 时，`logX < 0 < X`
2. 再看 `1 < X ≤ 2` 的情况，当 `X = 2` 时，`logX = 1 < X`，当 `1 < X < 2` 时，`logX < 1 < X`
3. 假设 `p < X ≤ 2p(p 是正整数)`，同时 `2p < Y ≤ 4p(p ≥ 1)`，则 `logY = log(2X) = log2 + logX = 1 + logX = 1 + log(Y/2)`
4. `1 + log(Y/2) < 1 + Y/2` 这是遵循归纳假设的 `logX < X`，由于 `2p < Y ≤ 4p(p ≥ 1)`，则 `1 < Y/2`，因此 `1 + Y/2 < Y/2 + Y/2`，显然 `Y/2 + Y/2 = Y`
5. 最后合并推理：`logY = 1 + log(Y/2) < 1 + Y/2 < Y/2 + Y/2 ≤ Y`，得：`logY < Y`

_第四步去套假设真的很秀，虽然不理解，但还是放过它。_

解惑(b)：

题目是：`logA^B = BlogA`。

1. 因为 `A^B = 2^(XB)` 所以 `log(A^B) = log(2^(XB))`
2. 设 `Y = log(2^(XB)) = log(A^B)` 转换为指数形式 `2^Y = 2^(XB)`，则 `Y = XB`
3. 代入 `Y = log(A^B)` 得 `XB = log(A^B)`
4. 又因 `2^X = A` 转为对数形式 `logA = X`，代入上述等式得 `BlogA=log(A^B)`，定理得证

_不难发现，`log2^X=X`，对这一条有点印象，因为读书的时候没少做题。_

## 1.8

英文答案：

> (a) The sum is 4/3 and follows directly from the formula.
> 
> (b) ![](images/ex1_8_b1.svg). ![](images/ex1_8_b2.svg). Subtracting the first equation from the second gives ![](images/ex1_8_b3.svg). By part (a), 3S = 4/3 so S = 4/9.
> 
> (c) ![](images/ex1_8_c1.svg). ![](images/ex1_8_c2.svg). Subtracting the first equation from the second gives ![](images/ex1_8_c3.svg). Rewriting, we get ![](images/ex1_8_c4.svg). Thus `3S = 2(4/9) + 4/3 = 20/9`. Thus `S = 20/27`.
> 
> (d) Let ![](images/ex1_8_d1.svg). Follow the same method as in parts (a)–(c) to obtain a formula for ![](images/ex1_8_d2.svg) in terms of ![](images/ex1_8_d3.svg) and solve the recurrence. Solving the recurrence is very difficult.

翻译：

- (a) 和是 `4/3`，直接由公式 ![](./images/series2_1.svg) 得出。
- (b) ![](images/ex1_8_b1.svg)。 ![](images/ex1_8_b2.svg)。用第二个方程减去第一个方程得到：![](images/ex1_8_b3_r.svg)（英文答案有误，翻译已纠正）。由 (a) 小节的结论，`3S = 4/3` 因此 `S = 4/9`。
- (c) ![](images/ex1_8_c1.svg)。 ![](images/ex1_8_c2.svg)。用第二个方程减去第一个方程得到：![](images/ex1_8_c3.svg)。重写后，我们得到：![](images/ex1_8_c4.svg)。因此，`3S = 2(4/9) + 4/3 = 20/9`，则，`S = 20/27`。
- (d) 假设 ![](images/ex1_8_d1.svg)。根据 (a) - (c) 小节的相同方法，得到在 ![](images/ex1_8_d3.svg) 中每一项的 ![](images/ex1_8_d2.svg) 表达式，并递归求解。解这个递归式非常困难。

## 1.9

英文答案：

> ![](./images/ex1_9.svg)

_提示：![](./images/min_x_max_integer.svg) 是小于或等于 `x` 的最大整数。_

解释：

1. 这一题用到了公式 ![](./images/algebraic_operation2.svg)
2. 前面学过 ![](./images/series7.svg)，则可得 `lnN - ln(N/2)`
3. 通过对数公式 ![](./images/logarithm_other1.svg) 可知，`N/(N/2) = 2`，则可估算为 `ln2`

## 1.10

英文答案：

> 2^4 = 16 ≡ 1 (mod 5). (2^4)^25 ≡ 1^25 (mod 5). Thus 2^100 ≡ 1 (mod 5).

解释：

1. 符号 `≡` 表示恒等式，即符号左边或右边的数，除以 `mod` 后面的数，所得结果（余数）相同
2. 选 `2^4` 是因为 `(2^4)^25 = 2^100`，且 `2^2 mod 5 = 4`，`2^3 mod 5 = 3`，都不满足余数为 `1` 的定义
3. 为什么是 `1` 呢？由传递性 `a ≡ b(mod p) 且 b ≡ c (mod p) ，则 a ≡ c(mod p)`，及定理 `AD ≡ BD(mod N)`，可知 `2^4*2^4 ≡ 1*2^4 ≡ 1(mod 5)`，以此类推 `2^4^N ≡ 2^4^(N-1) ≡ ... ≡ 2^4 ≡ 1(mod 5)`
4. 所以 `2^4^25 ≡ 2^4 ≡ 1(mod 5)`，从而得到 `2^100 ≡ 1 (mod 5)`

## 1.11

英文答案：

> (a) Proof is by induction. The statement is clearly true for `N = 1` and `N = 2`. Assume true for
`N = 1, 2, …, k`. Then ![](./images/ex1_11_1.svg). By the induction hypothesis, the value of the sum
on the right is ![](./images/ex1_11_2.svg), where the latter equality follows from the definition of
the Fibonacci numbers. This proves the claim for `N = k + 1`, and hence for all `N`.
> 
> (b) As in the text, the proof is by induction. Observe that `φ + 1= φ^2`. This implies that `φ^−1 + φ^−2 = 1`. For `N = 1` and `N = 2`, the statement is true. Assume the claim is true for `N = 1, 2, …, k`.
> 
> ![](./images/ex1_11_3.svg)
> 
> by the definition, and we can use the inductive hypothesis on the right-hand side, obtaining
> 
> ![](./images/ex1_11_4_1.svg)
> 
> ![](./images/ex1_11_4_2.svg)
> 
> ![](./images/ex1_11_4_3.svg)
>
> and proving the theorem.
> 
> (c) See any of the advanced math references at the end of the chapter. The derivation involves the use of generating functions.
 

翻译：

- (a) 归纳法证明。 对于 `N = 1` 和 `N = 2`，这种说法显然是成立的。 假设 `N = 1, 2, …, k` 成立。那么 ![](./images/ex1_11_1.svg) 。 根据归纳假设，右边的和是 ![](./images/ex1_11_2.svg)，后者的等式来源于斐波那契数的定义。这就证明了 `N = k+1` 的要求，因此对于所有的 `N` 都成立。
- (b) 正如文中所述，通过归纳法证明。观察 `φ + 1= φ^2`，这意味着 `φ^−1 + φ^−2 = 1`。对于 `N = 1` 和 `N = 2`，式子成立。假设式子对于 `N = 1, 2, …, k` 成立，则 ![](./images/ex1_11_3.svg)。根据定义，我们可以使用右边的归纳假设，得到：![](./images/ex1_11_4_1.svg)![](./images/ex1_11_4_2.svg)，则：![](./images/ex1_11_4_3.svg)，定理得证。
- (c) 请参阅本章末尾的高等数学参考资料。推导过程涉及到母函数的使用。

解惑：

- (a) 由 `F1 = 1, F2 = 2, F3 = 3` 知，当等式左边 `N - 2 = 1` 时，左边为 `F1 = 1`，右边为 `3 - 2 = 1`，符合等式，当等式左边 `N - 2 = 2` 时，左边为 `F1 + F2 = 3`，右边为 `5 - 2 = 3`，也符合条件。同时根据定义有 ![](./images/ex1_11_1.svg)，将它代入题目等式右边得 ![](./images/ex1_11_2.svg)，由此可知假设成立。
- (b) 由 `φ = (1+√5)/2` 得 `φ+1 = (1+√5+2)/2 = (3+√5)/2` 亦可得 `φ^2 = (1+5+2√5)/4 = (3+√5)/2`，此时可知 `φ + 1= φ^2`，将等式两边同时除以 `φ^2`，得 `φ^−1 + φ^−2 = 1`，随后开始归纳假设 ![](./images/ex1_11_4_1.svg)![](./images/ex1_11_4_2.svg)，这里用到指数公式 ![](./images/exponent1.svg)，合并得 ![](./images/ex1_11_4_3.svg)，从而证明式子成立。
- (c) 参见传送门答案。

_提示：这一题谷歌出来的答案比较靠谱，[传送门](http://ms.ntub.edu.tw/~spade/teaching/x-DS2005/DS-01-09.pdf) （注意，答案中有两个 (b)，第二个是 (c) 题的答案）。_

## 1.12

英文答案：

> (a) ![](./images/ex1_12_1.svg)
> 
> (b) The easiest way to prove this is by induction. The case `N = 1` is trivial. Otherwise,
> 
> ![](./images/ex1_12_2_1.svg)
> 
> ![](./images/ex1_12_2_2.svg)
> 
> ![](./images/ex1_12_2_3.svg)
> 
> ![](./images/ex1_12_2_4.svg)
> 
> ![](./images/ex1_12_2_5.svg)
> 
> ![](./images/ex1_12_2_6.svg)
> 
> ![](./images/ex1_12_2_7.svg)

解惑：

- (a) `2i` 的级数提取公因式为 2 倍 `i` 的级数，代入公式 ![](./images/series4.svg) 得 `N(N+1)`，而 `1` 的级数为 `N`——由公式 ![](./images/algebraic_operation1.svg) 得出，将它们代入等式即可证明公式。
- (b) 按照级数的定义代入 `N+1`，得 ![](./images/ex1_12_2_1.svg)，随后用公式 ![](./images/series4.svg) 代入题中右边的式子，即 ![](./images/ex1_12_2_2.svg)，接着提取公因式 `(N+1)^2`，则 ![](./images/ex1_12_2_3.svg)，之后变换式子得到 ![](./images/ex1_12_2_6.svg)，最后还是用公式 ![](./images/series4.svg) 代入得到 ![](./images/ex1_12_2_7.svg)，定理得证。

## 1.13

答案：[Collection.java](../../src/main/java/ch01/Collection.java)

## 1.14

答案：[OrderedCollection.java](../../src/main/java/ch01/OrderedCollection.java)

## 1.15

答案：[Rectangle.java](../../src/main/java/ch01/Rectangle.java)
