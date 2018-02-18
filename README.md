# Data Structure in Java

用Java实现的`java.util`的单线程数据结构和`java.util.concurrent`的多线程数据结构。并运行通过OpenJDK的测试文件。

## Single-thread Data Structure

### Priority Queue
`com.terryx.datastructure.mypriorityqueue` 

优先队列(堆)的基本操作主要有两个：add(offer) and remove(poll)。
- add (offer)
需要实现`siftUp`操作，原理很简单将新元素插入队尾然后每次和父亲节点进行比较，如果满足比较条件就一直向上移动。我们无须频繁进行`swap`操作，对于需要上移动的结点一直赋值给其子结点，最后将新结点放入循环结束时的指向位置。还有一个注意的地方是在调用时需要检查是否进行数组扩容，如果当前容量小于64就翻倍，否则只增长25%。

- remove (poll)
需要实现`siftDown`操作，原理是取出堆的末尾结点，然后一直与其两个子结点进行比较，对最满足比较条件的子结点向下移动。同理我们只需不停对父结点进行赋值，最后在停止位置放入所取出对结点。

- heapify
如果构造函数是对线性数据结构进行初始化，就需要这个操作，其复杂度比较特殊是O(n)。原理是对每个非叶子结点进行`siftDown`操作。

- removeAt
如果我们要删除给定下标的元素，首先拿去末尾结点进行`siftDown`，如果发现结果是末尾结点还处于删除位置，这就说明其祖先中可能存在不满足比较顺序的结点，我们就需要对其进行`siftUp`操作。

在研究源代码时我发现优先队列的iterator是可以满足删除并有序的，比如说`[1,2,3,4,5]`如果删除了2，4和5的位置就会打乱，并且如果继续遍历5会被跳过，因为已经放在了2的位置。这里Java是可以保证其不会影响遍历。Java给出的解决方法是：1. 在遍历时，通过另外一个队列`forgetMeNot`存储因打乱原顺序而移动到前面的元素。所以当遍历到最后的时候会再检查队列中是否存在元素。 2. 在删除时，判断删除的元素是不是会把最后一个元素换到前面，如果是就先进入队列中。如果删除的元素是存储在队列中时，需要线性时间扫描整个队列找到这个元素并删除。

### Hash Map
`com.terryx.datastructure.myhashmap`

哈希表（散列表）是一种最为常见的数据结构。因为其使用频度之高，Java对其进行的优化基本已经到了极致。本以为是一个非常容易实现的数据结构，但是其中也蕴含了很多“坑”。下面对原理和一些常见错误总结一下。

理论上哈希表的原理是非常简单粗暴的，一个hash函数，一个bucket存hashcode，每个slot再延伸出链表。但是实现上是有很多细节需要注意。

- hash函数：Java里是用32位整数来保存hashcode，但是如果单纯通过模运算会出现hash分布的不均匀增大碰撞几率。比如，对于32位浮点如果我们单纯对bucket的长度取模，对于部分浮点数低位部分基本相同会导致不同的浮点数发生碰撞。所以为了把高位和地位的信息同时应用到hash函数中，我们对高16位进行异或运算 `hash = (h = key.hashCode()) ^ (h >>> 16)`

- resize： 默认情况下，bucket的大小仅为16。当我们进行扩容时，naive的方法是对每个Node重新计算hash并重新储存的新的位置。这里Java进行了一个巧妙的优化，规定bucket的大小必须是2的指数形式，每次扩容翻倍。这样做的好处是可以通过新增加的二进制位来判断Node在扩容后的新位置。比如，当前长度是2^4 = 16，新长度是2^5 = 32。所以对于每个Node我们仅判断，hash的第五位是否为0，若为0则说明新的hash没有变化，还存储在这个slot上；若是1，就移动到新的位置 `oldIndex + newCap`。

- List to Tree： Java会检查list是否过长，如果超过某个threshold，就转变为红黑树。这样get的效率就从O(n)优化成了O(lg n)。这里因为红黑树的代码过于复杂，我在实现的时候就略去了这个优化。

- Null Key and Null Value： 这是Java HashMap的一个特殊的地方，它是可以存储空key和空value的。对于空key，hash为0，而且在`containsValue()`这个方法在实现的时候，需要额外先判断Node是否存在，不能单纯按照`Get()`的返回来判断，因为很有可能这个值就是null。

- Node： 这里Node的判断是通过hash和key来判断，二者缺一不可。

- Remove：需要判断是否是头结点，如果是就把next赋值给当前bucket的位置。

## Multi-thread Data Structure

### Blocking Queue
`com.terryx.datastructure.mypriorityqueue` 

使用two-condition算法分别使用两个Monitor和一个Lock控制队列为空和队列为满时进行不同的操作。这两个分别是`notFull`和`notEmpty`。如果队列被放满，`notFull`就会持有锁并等待，直到dequeue操作通知它队列已经不满。同样，`notEmpty`会这队列空的时候持有锁并等待，直到enqueue操作通知它队列已经不空。在Java中，Monitor是和Lock绑定的，而Monitor中存储了被等待线程，用来决定唤醒顺序。
