# 基础(Fundamentals)



## 1.1基础编程模型

### 算法(Algorithm)

​		**算法(algorithm)**是一种有限、确定、有效的并适合用计算机程序来实现的解决问题的方法。**数据结构(data structure)**是算法的副产品或是结果(data structures exist as the byproducts or end products of algorithms)。学习算法的主要原因是**它们能节约非常多的资源**，甚至能够让我们完成一些本不可能完成的任务。无论在任何应用领域，精心设计的算法都是解决大型问题最有效的方法。

​		在编写庞大或者复杂的程序时，理解和定义问题、控制问题的复杂度和将其分解为更容易解决的子问题需要大量的工作(When developing a huge or complex computer program, a great deal of effort must go into **understanding** and **defining** the problem to be solved, **managing its complexity**, and **decomposing it into smaller subtasks that can be implemented easily**)。很多时候，分解后的子问题所需的算法实现起来都比较简单。

​		为一项任务选择最合适的算法是困难的，这可能会需要复杂的数学分析。计算机科学中研究这种问题的分支叫做**算法分析(analysis of algorithms)**。通过分析，我们将要学习的许多算法都有着优秀的理论性能；而另一些我们则只是根据经验知道它们是可用的。



### 递归(Recursion)

编写递归代码时最重要的有以下三点。

* 递归总**有一个最简单的情况**——方法的第一条语句总是一个包含 return 的条件语句。
* 递归调用总是去尝试**解决一个规模更小的子问题**，这样递归才能收敛到最简单的情况。
* 递归调用的**父问题和尝试解决的子问题之间不应该有交集**。

> [万字长文带你彻底理解递归](https://bbs.huaweicloud.com/blogs/334084)



### 二分查找(Binary Search)







## 1.2 背包、队列和栈(Bags, Queues and Stacks)

​		许多基础数据类型都和对象的集合有关。具体来说，数据类型的值就是一组对象的集合，所有操作都是关于**添加**、**删除**或是**访问**集合中的对象(Several fundamental data types involve ***collections of objects***. Specifically, the set of values is a collection of objects, and the operations revolve around **adding**, **removing**, or **examining** objects in the collection. )。



### 背包(Bags)