echowand - Yet Another ECHONET Lite Library for Java
============================

Copyright (c) 2012 Yoshiki Makino

echowand について
---------------
Javaを用いた ECHONET Lite ライブラリです ECHONET Lite 規格を参考にしながら作成しています。

現在対応している通信方式は、ECHONET Lite 1.01 で規定されている UDP/IP のみです。

ECHONET Lite について
-------------------
ECHONET Lite は家電を制御することを目的とし、2011年に公開されたネットワークプロトコル規格です。詳しくは[エコーネットコンソーシアムのページ](http://www.echonet.gr.jp)をご覧ください。

使い方
-----
echowand.sample.Sample[0-4].java を参照してください。

* Sample0.java : フレームの送受信のサンプル
* Sample1.java : トランザクションの実行サンプル
* Sample2.java : LocalObjectを利用したサンプル
* Sample3.java : RemoteObjectを利用したサンプル
* Sample4.java : RemoteObjectの状変プロパティ取得のサンプル

注意点
-----
現在開発中であり、全てのクラスやメソッドについて、追加・削除・変更が行われる可能性があります。

出来る限り相互接続性も考慮して実装を行っていく予定ですが、本ライブラリを用いることで全ての ECHONET Lite 機器と正しく通信できることを保証するものではありません。