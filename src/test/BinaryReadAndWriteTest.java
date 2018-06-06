package test;

import java.io.*;

public class BinaryReadAndWriteTest {

    private void test() throws IOException {
        DataOutputStream os = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("src\\test\\test_binary.bin")));
        int i = 1024;
        os.writeInt(i);
        os.flush();
        os.close();

        DataInputStream is = new DataInputStream(new BufferedInputStream(new FileInputStream("src\\test\\test_binary.bin")));
        if (is.readInt() == i) {
            System.out.println("Test is OK :)");
        } else {
            System.out.println("There is something wrong :(");
        }
        is.close();
    }


    public static void main(String[] args) throws IOException {
        new BinaryReadAndWriteTest().test();
    }
}
