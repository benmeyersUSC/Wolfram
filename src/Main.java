
public class Main {
    public static void main(String[] args) {
//        int[] nums = {30, 110, 45, 99, 73, 75, 22, 62, 86, 105, 124, 150, 182};
        int[] nums = {30, 110, 99};
        for (int num: nums){
            CellularAutomata CA = new CellularAutomata(num);
            CA.display();
//            CA.run_switch();
            CA.saveAsImage();
        }
    }
}