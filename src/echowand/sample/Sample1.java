package echowand.sample;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.logic.*;
import echowand.net.*;


/**
 * トランザクションが受信した応答メッセージの処理を行うクラス。
 * メッセージの中身を表示する。
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

/**
 * トランザクションを行うサンプルプログラム
 * @author Yoshiki Makino
 */
public class Sample1 {
    public static void main(String[] args) {
        // ECHONET Liteメッセージ送受信に利用するIPのサブネットを作成
        InetSubnet subnet = new InetSubnet();
        
        // トランザクション管理オブジェクトを生成
        TransactionManager transactionManager = new TransactionManager(subnet);
        
        // メインループオブジェクトを生成
        MainLoop loop = new MainLoop();
        // サブネットを登録
        loop.setSubnet(subnet);
        // トランザクション管理オブジェクトを登録
        // これによりトランザクション管理オブジェクトが応答メッセージを受信するようになる
        loop.addListener(transactionManager);
        
        // メインループをスレッドで開始
        Thread loopThread = new Thread(loop);
        loopThread.setDaemon(true);
        loopThread.start();
        
        // トランザクションを作成
        Transaction t1 = createTransactionGet(subnet, transactionManager);
        Transaction t2 = createTransactionSet(subnet, transactionManager);
        Transaction t3 = createTransactionSetGet(subnet, transactionManager);
        
        try {
            // トランザクションを実行
            t1.execute();
            t2.execute();
            t3.execute();
            
            // トランザクションの終了待ち
            t1.join();
            t2.join();
            t3.join();
        } catch (SubnetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static Transaction createTransactionGet(Subnet subnet, TransactionManager transactionManager) {
        // Set、Get、SetGetトランザクションの設定オブジェクトを作成
        SetGetTransactionConfig transactionConfig = new SetGetTransactionConfig();
        // 送信元ノードの設定
        transactionConfig.setSenderNode(subnet.getLocalNode());
        // 宛先ノードをグループに設定
        transactionConfig.setReceiverNode(subnet.getGroupNode());
        // 送信元オブジェクトを温度センサオブジェクトに設定
        transactionConfig.setSourceEOJ(new EOJ("001101"));
        // 宛先オブジェクトをノードプロファイルオブジェクトに設定
        transactionConfig.setDestinationEOJ(new EOJ("0EF001"));
        // EPCが0x80のプロパティに対してGetを行うように設定
        transactionConfig.addGet(EPC.x80);
        // トランザクション管理オブジェクトを利用してトランザクションオブジェクトを生成。
        Transaction transaction = transactionManager.createTransaction(transactionConfig);
        // トランザクションのタイムアウト時間をミリ秒単位で設定
        transaction.setTimeout(1000);
        // 応答処理のためのリスナーオブジェクトを登録
        transaction.addTransactionListener(new Sample1Listener());
        
        return transaction;
    }

    public static Transaction createTransactionSet(Subnet subnet, TransactionManager transactionManager) {
        // Set、Get、SetGetトランザクションの設定オブジェクトを作成
        SetGetTransactionConfig transactionConfig = new SetGetTransactionConfig();
        // 送信元ノードの設定
        transactionConfig.setSenderNode(subnet.getLocalNode());
        // 宛先ノードをグループに設定
        transactionConfig.setReceiverNode(subnet.getGroupNode());
        // 送信元オブジェクトを温度センサオブジェクトに設定
        transactionConfig.setSourceEOJ(new EOJ("001101"));
        // 宛先オブジェクトをエアコンオブジェクトに設定
        transactionConfig.setDestinationEOJ(new EOJ("013001"));
        // EPCが0x81のプロパティに対して0x30(動作状態ON)というデータをSetするように設定
        transactionConfig.addSet(EPC.x80, new Data((byte) 0x30));
        // 応答がなくてもトランザクションは成功したこととする
        transactionConfig.setResponseRequired(false);
        // トランザクション管理オブジェクトを利用してトランザクションオブジェクトを生成。
        Transaction transaction = transactionManager.createTransaction(transactionConfig);
        // トランザクションのタイムアウト時間をミリ秒単位で設定
        transaction.setTimeout(1000);
        // 応答処理のためのリスナーオブジェクトを登録
        transaction.addTransactionListener(new Sample1Listener());
        
        return transaction;
    }

    public static Transaction createTransactionSetGet(Subnet subnet, TransactionManager transactionManager) {
        // Set、Get、SetGetトランザクションの設定オブジェクトを作成
        SetGetTransactionConfig transactionConfig = new SetGetTransactionConfig();
        // 送信元ノードの設定
        transactionConfig.setSenderNode(subnet.getLocalNode());
        // 宛先ノードをグループに設定
        transactionConfig.setReceiverNode(subnet.getGroupNode());
        // 送信元オブジェクトを温度センサオブジェクトに設定
        transactionConfig.setSourceEOJ(new EOJ("001101"));
        // 宛先オブジェクトをエアコンオブジェクトに設定
        transactionConfig.setDestinationEOJ(new EOJ("013001"));
        // EPCが0xB0のプロパティに対して0x41(自動運転モード)というデータをSetするように設定
        transactionConfig.addSet(EPC.xB0, new Data((byte) 0xff));
        // EPCが0x88のプロパティに対してGetを行うように設定
        transactionConfig.addGet(EPC.x88);
        // 応答がなくてもトランザクションは成功したこととする
        transactionConfig.setResponseRequired(false);
        // トランザクション管理オブジェクトを利用してトランザクションオブジェクトを生成。
        Transaction transaction = transactionManager.createTransaction(transactionConfig);
        // トランザクションのタイムアウト時間をミリ秒単位で設定
        transaction.setTimeout(1000);
        // 応答処理のためのリスナーオブジェクトを登録
        transaction.addTransactionListener(new Sample1Listener());
        
        return transaction;
    }
}
