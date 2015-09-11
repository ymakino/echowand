echowand - Yet Another ECHONET Lite Library for Java
============================

Copyright (c) 2012-2015 Yoshiki Makino

echowand について
---------------
Javaを用いた ECHONET Lite ライブラリです。ECHONET Lite 規格を参考にして作成しています。
現在対応している通信方式は、ECHONET Lite 1.1 で規定されている UDP/IP と TCP/IP です。
TCP/IPについては実装は行ってみましたが他システムやデバイスとの通信の試験は行っておりませんので
ご注意ください。

echowandはローカルオブジェクトを実装したECHONET Lite デバイスの作成や、リモートオブジェクトの
制御を行うコントローラを簡単に作成するために利用可能なライブラリとなっています。
ここでは、ライブラリを動作させるシステム上で管理されているECHONET Lite オブジェクトのことを
ローカルオブジェクト、ネットワーク接続された外部のECHONET Lite デバイス上で管理されている
ECHONET Lite オブジェクトのことをリモートオブジェクトと呼ぶことにします。

echowandはフレームの送受信からSETやGET、状変時アナウンスの処理やECHONET Lite オブジェクトの
抽象化までECHONET Liteで必要となる処理の実装を行った様々なクラスからなっていますが、
ライブラリの一部のクラスだけを利用することも可能です。また、様々な処理を担うオブジェクトを
入れ替えることで動作のカスタマイズが可能な設計となっています。

ECHONET Lite について
-------------------
ECHONET Lite は家電を制御することを目的とし、2011年に公開されたネットワークプロトコル規格です。
詳しくは[エコーネットコンソーシアムのページ](http://www.echonet.gr.jp)をご覧ください。

###ECHONET Lite 規格認証###
ECHONET Lite 規格に適合し相互接続性を保証するために
[エコーネットコンソーシアム](http://www.echonet.gr.jp)では
[認証制度](http://www.echonet.gr.jp/kikaku_ninsyo/index.htm)を設けています。

これまで、様々なコントローラやデバイスを作成したりECHONET Liteを利用した実験を行うために
echowandを利用していますが、ECHONET Lite 規格認証の申請を行ったことはありません。
また、echowandを利用して作成されたデバイス等が ECHONET Lite 認証を取得することも
可能だと思いますが、現在まで認証を通したという報告は受けていません。

echowandの基本的な使い方
---------

###`Core`の生成と初期化###
`echowand.service.Core`クラス(以下`Core`とする)を利用することでライブラリの初期化を
容易に行うことが可能です。もし個別の機能を独立して利用する場合には、`Core`を利用せずに
必要なクラスのインスタンスを個別に生成し初期化を行う必要があります。
ここでは`Core`を利用したライブラリの初期化について説明します。

`Core`を初期化する際には、利用する`echowand.net.Subnet`クラス(以下`Subnet`とする)の
インスタンスを指定します。
例として、ここではIPv4を利用する`Subnet`である`echowand.net.Inet4Subnet`クラス
(以下`Inet4Subnet`とする)を用いて初期化を行います。
`Inet4Subnet`はインスタンスの生成後、送受信処理を開始するために`startService`メソッドが
呼び出される必要があります。便利のため、インスタンスの生成とstartService呼び出しを両方行う
`startSubnet`という静的メソッドが定義されています。
ここでは`startSubnet`メソッドを利用することにします。

```java
Core core = new Core(Inet4Subnet.startSubnet());
```

マルチキャストパケット送信に利用するネットワークインタフェースを指定する場合には、
startSubnetメソッドの引数としてネットワークインタフェースのインスタンスを与えます。

```java
NetworkInterface nif = NetworkInterface.getByName("eth0");
Core core = new Core(Inet4Subnet.startSubnet(nif));
```

###ローカルオブジェクト情報の作成###
ECHONET Liteのローカルオブジェクト情報の作成を行います。ここで設定した情報に基づき
ローカルオブジェクトが生成されます。
ローカルオブジェクトを利用しないコントローラを実装する場合には本節はスキップしてください。

ローカルオブジェクトを生成するだけでもプロパティデータの送受信は可能ですが、
特殊な処理を行うためには`echowand.object.LocalObjectDelegate`や
`echowand.service.PropertyDelegate`(以下`PropertyDelegate`とする)を継承したクラスで
定義します。
ここでは、`PropertyDelegate`を利用し`/sys/class/thermal/thermal_zone0/temp`を
温度として返す温度センサを作成します。

```java
class SampleLocalObjectPropertyDelegate extends PropertyDelegate {

    public SampleLocalObjectPropertyDelegate() {
        super(EPC.xE0, true, false, false);
    }

    @Override
    public ObjectData getUserData(LocalObject object, EPC epc) {
        try {
            FileReader fr = new FileReader("/sys/class/thermal/thermal_zone0/temp");
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            int value = (int) (Double.parseDouble(line) * 10 / 1000);
            byte b1 = (byte) ((value >> 8) & 0xff);
            byte b2 = (byte) (value & 0xff);
            return new ObjectData(b1, b2);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
```

ローカルオブジェクト情報を作成するには、最初に`echowand.service.LocalObjectConfig`
(以下`LocalObjectConfig`とする)のインスタンスを生成します。
作成するローカルオブジェクトのEPCやEOJ等に関する情報は`echowand.info.ObjectInfo`を
継承したクラスを利用し、`LocalObjectConfig`のコンストラクタに渡します。
温度センサ用の情報はすでに`echowand.info.TemperatureSensorInfo`として作成されているので、
それを利用します。
また、先ほど作成した`LocalObjectConfig`に`PropertyDelegate`を登録することで、
温度を適切に取得するように設定します。

```java
TemperatureSensorInfo info = new TemperatureSensorInfo();
LocalObjectConfig config = new LocalObjectConfig(info);
config.addPropertyDelegate(new SampleLocalObjectPropertyDelegate());
```

`Core`に`LocalObjectConfig`を登録します。

```java
core.addLocalObjectConfig(config);
```

ここではローカルオブジェクトの生成に必要な情報を与えるだけであり、実際にローカルオブジェクトが
生成されるのは、`Core`の`startService`が呼び出された時になります。
ここで記述した温度センサの動作プログラムは
[echowand.sample.SampleLocalObject.java](https://github.com/ymakino/echowand/blob/master/src/echowand/sample/SampleLocalObject.java)
にあります。
このプログラムはRaspberry PiのRaspbianで動作することを確認済みです。(2015/9/9時点)

###`Core`の処理を開始###
ここまでの処理で`Core`の作成は完了しますがローカルオブジェクトの生成やその他の初期化等は
行われていません。
`startService`メソッドを呼び出すことで初期化が行われネットワークの送受信処理を開始します。

```java
core.startService();
```

`echowand.service.Service`(以下`Service`とする)のインスタンスを利用することで、
ライブラリの多くの機能を利用可能となります。
`Service`のインスタンスは、利用するCoreを引数としてコンストラクタを呼び出すことで行います。

```java
Service service = new Service(core);
```

###`doGet`を利用したGET処理###
`Service`の`doGet`メソッドを利用する方法を説明します。

与える引数の異なる`doGet`メソッドが複数あるので、適切な`doGet`を選択します。
ここでは、IPアドレスが192.168.0.1のECHONET Liteノードのエアコンの動作状況について
確認する方法を説明します。
エアコンのEOJは013001で、動作状況を表すEPCは0x80です。
また、応答を受信するまでのタイムアウトを1秒とします。

`doGet`メソッドは`echowand.service.result.GetResult`クラス(以下`GetResult`とする)の
インスタンスを返します。
`GetResult`にはGETのリクエストとレスポンスに関する情報が蓄えられます。

```java
Node node = service.getRemoteNode("192.168.0.1");
EOJ eoj = new EOJ("013001");
EPC epc = EPC.x80;
GetResult result = service.doGet(node, eoj, epc, 1000);
```

このメソッドは応答を待つことなく終了します。
現在の実行状況の確認は`GetResult`の`isDone`メソッドで確認できます。応答タイムアウトの前までは
falseで、その後はtrueを返します。

応答タイムアウトを待ってから処理を行う場合には`join`メソッドを利用できます。`join`を呼び出すと
応答タイムアウトが発生するまで処理をブロックします。

レスポンスのプロパティデータを調べるときには`countData`、`getData`、`getDataList`を利用します。
レスポンスのフレームについて調べるときには`countFrames`、`getFrame`、`getFrameList`を利用します。
例として、レスポンスで取得したデータについて表示するプログラム例を以下に示します。

```java
result.join();

for (int i=0; i<result.countData(); i++) {
    System.out.println("Data " + i + " :" + result.getData(i));
}
```

以上の動作プログラムは
[echowand.sample.SampleDoGet.java](https://github.com/ymakino/echowand/blob/master/src/echowand/sample/SampleDoGet.java)
にあります。


###`doSet`を利用したSET処理###
ここでは`Service`の`doSet`メソッドを利用する方法を説明します。

与える引数の異なる`doSet`メソッドが複数あるので、適切な`doSet`を選択します。
ここでは、GET処理で説明したエアコンを利用し、エアコンの動作を開始させる方法について説明します。
`doGet`と異なり、プロパティに設定する値を`echowand.common.Data`クラス
(以下`Data`クラスとする)も指定します。
動作を開始する際には`Data`として0x30を与え、停止する場合には0x31を渡します。

`doSet`メソッドは`echowand.service.result.SetResult`クラス(以下`SetResult`とする)の
インスタンスを返します。
`SetResult`にはSETのリクエストとレスポンスに関する情報が蓄えられます。

```java
Node node = service.getRemoteNode("192.168.0.1");
EOJ eoj = new EOJ("013001");
EPC epc = EPC.x80;
Data data = new Data((byte)0x30);
SetResult result = service.doSet(node, eoj, epc, data, 1000);
```

例として、レスポンスで取得したデータについて表示するプログラム例を以下に示します。
SETに成功したプロパティについては、空のデータをレスポンスとして受信することになります。

```java
result.join();

for (int i=0; i<result.countData(); i++) {
    System.out.println("Data " + i + " :" + result.getData(i));
}
```

以上の動作プログラムは
[echowand.sample.SampleDoSet.java](https://github.com/ymakino/echowand/blob/master/src/echowand/sample/SampleDoSet.java)
にあります。

###`doObserve`を利用した状変時アナウンス受信処理###
`Service`の`doObserve`メソッドを利用する方法を説明します。

与える引数の異なる`doObserve`メソッドが複数あるので、適切な`doObserve`を選択します。
ここでは、GET処理で説明したエアコンを利用し、エアコンの状変時アナウンス受信時の処理について説明します。
`doGet`と異なり、タイムアウト時間の設定はありません。

`doObserve`メソッドは`echowand.service.result.ObserveResult`クラス
(以下`ObserveResult`とする)のインスタンスを返します。
`ObserveResult`には、`doObserve`メソッドで指定した送信元から送られた状変時アナウンスの
データの情報が蓄えられます。

```java
Node node = service.getRemoteNode("192.168.0.1");
EOJ eoj = new EOJ("013001");
EPC epc = EPC.x80;
ObserveResult result = service.doObserve(node, eoj, epc);
```

レスポンスのプロパティデータを調べるときには`countData`、`getData`、`getDataList`を利用します。
レスポンスのフレームについて調べるときには`countFrames`、`getFrame`、`getFrameList`を利用します。

例として、10秒間で受信した状変時アナウンスのデータについて表示するプログラム例を以下に示します。
        
```java
Thread.sleep(10000);

for (int i=0; i<result.countData(); i++) {
    System.out.println("Data " + i + " :" + result.getData(i));
}
```

`ObserveResult`の状変時アナウンスの受信を停止する場合には、`stopObserve`メソッドを呼び出します。

```java
result.stopObserve();
```

以上の動作プログラムは
[echowand.sample.SampleDoObserve.java](https://github.com/ymakino/echowand/blob/master/src/echowand/sample/SampleDoObserve.java)
にあります。

###`RemoteObject`を利用したGET、SET処理###
GETやSETの対象となるリモートオブジェクトが1つ、さらに一度にSETやGETを行うEPCの数が1つの場合には
`echowand.object.RemoteObject`(以下`RemoteObject`とする)を利用して処理を行うことも可能です。

ECHONET Liteネットワーク内のリモートオブジェクトの一つ一つに対応する`RemoteObject`の
インスタンスを生成し、そのインスタンスを利用することでGETやSET等の処理が可能となります。

`RemoteObject`は先に`Core`内の`echowand.object.RemoteObjectManager`に
登録されている必要があります。
`Service`の`doUpdateRemoteInfo`メソッドを利用することで、ローカルネットワークに存在する
リモートオブジェクトの登録を自動的に行うことも可能ですが、
ここでは、192.168.0.1のエアコンを手動で登録することにします。

```java
Node node = service.getRemoteNode("192.168.0.1");
EOJ eoj = new EOJ("013001");
service.registerRemoteEOJ(node, eoj);
```

RemoteObjectが上書きされてしまうため、同一のオブジェクトの登録を複数回行わないように注意してください。
登録が終わったので`RemoteObject`を取得することが可能となります。

```java
RemoteObject remoteObject = service.getRemoteObject(node, eoj);
```

`RemoteObject`の`getData`メソッドを利用してデータをGETできます。`getData`は
`ObjectData`クラスのインスタンスを返します。

```java
EPC epc = EPC.x80;
ObjectData data = remoteObject.getData(epc);
```

また、同様に`setData`メソッドを利用してデータのSETができます。

```java
EPC epc = EPC.x80;
ObjectData data = new ObjectData((byte)0x30);
boolean result = remoteObject.setData(epc, data);
```

以上の動作プログラムは
[echowand.sample.SampleRemoteGet.java](https://github.com/ymakino/echowand/blob/master/src/echowand/sample/SampleRemoteGet.java)
と
[echowand.sample.SampleRemoteSet.java](https://github.com/ymakino/echowand/blob/master/src/echowand/sample/SampleRemoteSet.java)
にあります。

###`RemoteObject`を利用した状変時アナウンス処理###
`RemoteObject`を利用した状変時アナウンス処理を行うことも可能です。

状変時アナウンスの処理を行うためには`echowand.object.RemoteObjectObserver`
(以下`RemoteObjectObserver`とする)を継承したクラスを作成し`RemoteObject`に登録して利用します。

ここでは`RemoteObjectObserver`を継承した匿名クラスのインスタンスを作成し、`RemoteObject`に登録してみます。
この匿名クラスは状変時アナウンスのデータを出力します。

```java
Node node = service.getRemoteNode("192.168.0.1");
EOJ eoj = new EOJ("013001");
RemoteObject remoteObject = service.getRemoteObject(node, eoj);

remoteObject.addObserver(new RemoteObjectObserver() {
    @Override
    public void notifyData(RemoteObject object, EPC epc, ObjectData data) {
        System.out.println(object.getNode() + " " + object.getEOJ() + " " + epc + " " + data);
    }
});
```

以上の動作プログラムは
[echowand.sample.SampleRemoteObserver.java](https://github.com/ymakino/echowand/blob/master/src/echowand/sample/SampleRemoteObserver.java)
にあります。

###その他の機能###
`Service`には他にも以下のような機能があります。

* `doSetGet`: SetGet処理を実行
* `doNotify`: 状変時アナウンスの送信
* `doCapture`: 全送受信フレームの取得 (本機能を利用するためには`Subnet`として`CaptureSubnet`を利用する)
* `doUpdateRemoteInfo`: ローカルネットワーク内のリモートオブジェクト情報を収集しCoreに登録
* `getLocalData`: ローカルオブジェクトのデータ取得
* `setLocalData`: ローカルオブジェクトのデータ設定
* `getRemoteData`: `RemoteObject`を用いてリモートオブジェクトのデータ取得
* `setRemoteData`: `RemoteObject`を用いてリモートオブジェクトのデータ設定
* その他

`Service`や`Core`を利用しないechowandの使い方
-----
echowand.sample.Sample[0-4].java を参照してください。

* [Sample0.java](https://github.com/ymakino/echowand/blob/master/src/echowand/sample/Sample0.java) : フレームの送受信のサンプル
* [Sample1.java](https://github.com/ymakino/echowand/blob/master/src/echowand/sample/Sample1.java) : トランザクションの実行サンプル
* [Sample2.java](https://github.com/ymakino/echowand/blob/master/src/echowand/sample/Sample2.java) : LocalObjectを利用したサンプル
* [Sample3.java](https://github.com/ymakino/echowand/blob/master/src/echowand/sample/Sample3.java) : RemoteObjectを利用したサンプル
* [Sample4.java](https://github.com/ymakino/echowand/blob/master/src/echowand/sample/Sample4.java) : RemoteObjectの状変プロパティ取得のサンプル

添付アプリケーション
-----
echowandを利用して作成したリモートオブジェクトの表示ツールを添付しています。

ECHONET Lite ネットワークに接続し、`echowand.app.ObjectViewer`を起動してみてください。
起動時にネットワークインタフェースを聞かれるので、ECHONET Liteネットワークに
接続しているインタフェースを選択してください。

このアプリケーションの主な機能は以下の通りです。

* ECHONET LiteデバイスのIPアドレスを左上のリストに表示
* 選択されたデバイス中に存在するEOJのリストを右上のリストに表示
* 選択されたEOJオブジェクトの情報を下部のテーブルに表示
* 右上のEOJをダブルクリックすると、指定されたEOJの情報を新規ウィンドウで表示
* Set可能なプロパティは、テーブルのセルをダブルクリックすることで編集可能(16進数で記述)

注意点
-----
現在開発中であり、全てのクラスやメソッドについて、追加・削除・変更が行われる可能性があります。

相互接続性も考慮して実装を行っていく予定ですが、本ライブラリを用いることで
全ての ECHONET Lite 機器と正しく通信できることを保証するものではありません。
