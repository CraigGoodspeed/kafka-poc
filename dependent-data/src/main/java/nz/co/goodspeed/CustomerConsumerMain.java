package nz.co.goodspeed;

import java.io.IOException;

public class CustomerConsumerMain {


    public static void main(String[] args) throws IOException {
        startConsumer();

    }

    public static void startConsumer() {
        Thread consumeData = new Thread(new CustomerConsumer());
        Thread consumeCustomerProductPrice = new Thread(new CustomerProductPriceConsumer());
        consumeData.start();
        consumeCustomerProductPrice.start();
    }



}