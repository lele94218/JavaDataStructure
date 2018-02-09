# Data Structure in Java

用Java实现的`java.util`的单线程数据结构和`java.util.concurrent`的多线程数据结构。并运行OpenJDK的测试文件。

## Single-thread Data Structure

### Priority Queue
`com.terryx.datastructure.mypriorityqueue` 优先队列(堆)的基本操作主要有两个：add(offer) and remove(poll)。
- add (offer)
需要实现`siftUp`操作，原理很简单将新元素插入队尾然后每次和父亲节点进行比较，如果满足比较条件就一直向上移动。我们无须频繁进行`swap`操作，对于需要上移动的结点一直赋值给其子结点，最后将新结点放入循环结束时的指向位置。
还有一个注意的地方是在调用时需要检查是否进行数组扩容，如果当前容量小于64就翻倍，否则只增长25%。

- remove (poll)
需要实现`siftDown`操作，原理是取出堆的末尾结点，然后一直与其两个子结点进行比较，对最满足比较条件的子结点向下移动。同理我们只需
不停对父结点进行赋值，最后在停止位置放入所取出对结点。

- heapify
如果构造函数是对线性数据结构进行初始化，就需要这个操作，其复杂度比较特殊是O(n)。原理是对每个非叶子结点进行`siftDown`操作。

- removeAt
如果我们要删除给定下标的元素，首先拿去末尾结点进行`siftDown`，如果发现结果是末尾结点还处于删除位置，这就说明其祖先中可能存在不满足比较顺序
的结点，我们就需要对其进行`siftUp`操作。

在研究源代码时我发现优先队列的iterator是可以满足删除并有序的，比如说`[1,2,3,4,5]`如果删除了2，4和5的位置就会打乱，并且如果继续遍历5会被跳过，
因为已经放在了2的位置。这里Java是可以保证其不会影响遍历。

## Multi-thread Data Structure

### Blocking Queue
`com.terryx.datastructure.mypriorityqueue` 使用two-condition算法分别使用两个Monitor和一个Lock控制队列为空和队列为满时进行不同的操作。这两个
分别是`notFull`和`notEmpty`。如果队列被放满，`notFull`就会持有锁并等待，直到dequeue操作通知它队列已经不满。同样，`notEmpty`会这队列
空的时候持有锁并等待，直到enqueue操作通知它队列已经不空。在Java中，Monitor是和Lock绑定的，而Monitor中存储了被等待线程，用来决定唤醒顺序。