package nz.co.goodspeed;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;

public class CustomPrint extends PrintStream {

    public CustomPrint(OutputStream out) {
        super(out);
    }

    @Override
    public void println(String string) {
        Date date = new Date();
        super.println("[" + date.toString() + "] " + string);
    }

    @Override
    public PrintStream printf(String format, Object ... args) {
        Date date = new Date();
        return super.printf("["+date.toString()+"] "+format, args);
    }
}
