package echowand.sample;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.logic.*;
import echowand.net.*;


/**
 *
 * @author Yoshiki Makino
 */
class Sample1Listener implements TransactionListener {
    private int getOPC(StandardPayload payload, int sw) {
        switch (sw) {
            case 0: return payload.getFirstOPC();
            case 1: return payload.getSecondOPC();
        }
        return -1;
    }
    private Property getPropertyAt(StandardPayload payload, int index, int sw) {
        switch (sw) {
            case 0: return payload.getFirstPropertyAt(index);
            case 1: return payload.getSecondPropertyAt(index);
        }
        return null;
    }
    private void printProperties(StandardPayload payload, int sw) {
        for (int i=0; i<getOPC(payload, sw); i++) {
            Property p = getPropertyAt(payload, i, sw);
            System.out.format("    EPC: 0%s\n", p.getEPC());
            System.out.format("    PDC: 0x%02x\n", p.getPDC());
            if (p.getPDC() > 0) {
                System.out.format("    EDT: %s", p.getEDT());
            }
        }
    }
    @Override
    public void begin(Transaction t) {
        System.out.println("begin: " + t.getTID());
    }
    @Override
    public void receive(Transaction t, Subnet subnet, Frame frame) {
        CommonFrame cf = frame.getCommonFrame();
        StandardPayload payload = (StandardPayload)cf.getEDATA();
        System.out.println("From: " + frame.getSender());
        System.out.println("To: " + frame.getReceiver());
        System.out.println("  TID: " + cf.getTID());
        System.out.println("  SEOJ: " + payload.getSEOJ());
        System.out.println("  DEOJ: " + payload.getDEOJ());
        System.out.print("  ESV: " + payload.getESV());
        System.out.format("(0x%02x)\n", payload.getESV().toByte());
        System.out.println("  OPC: " + payload.getFirstOPC());
        printProperties(payload, 0);
        if (payload.getSecondOPC() > 0) {
            System.out.println("  OPC: " + payload.getSecondOPC());
            printProperties(payload, 1);
        }
    }
    @Override
    public void finish(Transaction t) {
        System.out.println("finish: " + t.getTID());
    }
}

public class Sample1 {
    public static void main(String[] args) {
        InetSubnet subnet = new InetSubnet();
        TransactionManager transactionManager = new TransactionManager(subnet);
        MainLoop loop = new MainLoop();
        loop.setSubnet(subnet);
        loop.addListener(transactionManager);
        
        Thread loopThread = new Thread(loop);
        loopThread.setDaemon(true);
        loopThread.start();

        Transaction t1 = createTransaction1(subnet, transactionManager);
        Transaction t2 = createTransaction2(subnet, transactionManager);
        Transaction t3 = createTransaction3(subnet, transactionManager);
        try {
            t1.execute();
            t2.execute();
            t3.execute();
            t1.join();
            t2.join();
            t3.join();
        } catch (SubnetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static Transaction createTransaction1(Subnet subnet, TransactionManager transactionManager) {
        SetGetTransactionConfig transactionConfig = new SetGetTransactionConfig();
        transactionConfig.setSenderNode(subnet.getLocalNode());
        transactionConfig.setReceiverNode(subnet.getGroupNode());
        transactionConfig.setSourceEOJ(new EOJ("001101"));
        transactionConfig.setDestinationEOJ(new EOJ("0EF001"));
        transactionConfig.addGet(EPC.x80);
        Transaction transaction = transactionManager.createTransaction(transactionConfig);
        transaction.setTimeout(1000);
        transaction.addTransactionListener(new Sample1Listener());
        return transaction;
    }

    public static Transaction createTransaction2(Subnet subnet, TransactionManager transactionManager) {
        SetGetTransactionConfig transactionConfig = new SetGetTransactionConfig();
        transactionConfig.setSenderNode(subnet.getLocalNode());
        transactionConfig.setReceiverNode(subnet.getGroupNode());
        transactionConfig.setSourceEOJ(new EOJ("001101"));
        transactionConfig.setDestinationEOJ(new EOJ("013001"));
        transactionConfig.addSet(EPC.x81, new Data((byte) 0x42));
        transactionConfig.setResponseRequired(false);
        Transaction transaction = transactionManager.createTransaction(transactionConfig);
        transaction.setTimeout(1000);
        transaction.addTransactionListener(new Sample1Listener());
        return transaction;
    }

    public static Transaction createTransaction3(Subnet subnet, TransactionManager transactionManager) {
        SetGetTransactionConfig transactionConfig = new SetGetTransactionConfig();
        transactionConfig.setSenderNode(subnet.getLocalNode());
        transactionConfig.setReceiverNode(subnet.getGroupNode());
        transactionConfig.setSourceEOJ(new EOJ("001101"));
        transactionConfig.setDestinationEOJ(new EOJ("013001"));
        transactionConfig.addSet(EPC.xB0, new Data((byte) 0xff));
        transactionConfig.addGet(EPC.x81);
        transactionConfig.setResponseRequired(false);
        Transaction transaction = transactionManager.createTransaction(transactionConfig);
        transaction.setTimeout(1000);
        transaction.addTransactionListener(new Sample1Listener());
        return transaction;
    }
}
