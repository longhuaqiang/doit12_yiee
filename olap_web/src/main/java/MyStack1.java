import java.util.Stack;

/**
 * Created by ChenLongWen on 2020/4/15.
 */
public class MyStack1 {
    private Stack<Integer> stackData;
    private Stack<Integer> stackMin;

    public MyStack1() {
        this.stackData = new Stack<Integer>();
        this.stackMin = new Stack<Integer>();
    }

    @Override
    public String toString() {
        return "MyStack1{" +
                "stackData=" + stackData +
                ", stackMin=" + stackMin +
                '}';
    }

    public void push(int newNum) {
        if (this.stackMin.isEmpty()) {
            this.stackMin.push(newNum);

        } else if (newNum <= this.getMin()) {
            this.stackMin.push(newNum);

        }
        this.stackData.push(newNum);

    }

    //返回stackData的栈顶元素

    /**
     * 出栈规则:当stackData弹出数据之后,stackMin也要做做相应的调整
     * @return
     */
    public int pop() {
        if (this.stackData.isEmpty()) {
            throw new RuntimeException("Your stack is empty.");
        }
        int value = this.stackData.pop();
        if (value == this.getMin()) {
            this.stackMin.pop();

        }
        return value;
    }

    public int getMin() {
        if (this.stackMin.isEmpty()) {
            throw new RuntimeException("Your stack is empty.");
        }
        return this.stackMin.peek();
    }


    /**
     * 测试:
     * @param args
     */
    public static void main(String[] args) {
        MyStack1 myStack1 = new MyStack1();
        int[] arr = {5,7,6,1,2,3,8};
        for (int i = 0; i < arr.length;i++){
            myStack1.push(arr[i]);

        }
        System.out.println("最小值:" + myStack1.pop());
    }

}
