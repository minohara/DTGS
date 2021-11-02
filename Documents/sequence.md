# シーケンス図

## サーバ開始時

1．両方のサーバが動いているとき
```plantuml
hide unlinked
control "タイマー 1" as Timer1
participant 第1サーバ as PS
participant 第2サーバ as SS
control "タイマー 2" as Timer2
activate PS
activate SS
note over PS:非同期サーバソケット\n\
を作り，接続を待つ
PS -> Timer1:set
deactivate PS
activate Timer1
note over SS:非同期ソケットを作り，\n\
第1サーバに接続する

PS <- SS:connect
deactivate SS
activate PS
PS -> Timer1:reset
deactivate Timer1
PS --> SS:ok
activate SS
note over PS,SS: 非同期サーバソケットを作り\n\
クライアントからの接続を待つ
```

2．第1サーバのみが動いているとき
```plantuml
hide unlinked
control "タイマー 1" as Timer1
participant 第1サーバ as PS
participant 第2サーバ as SS
control "タイマー 2" as Timer2
activate PS
note over PS:非同期サーバソケット\n\
を作り，接続を待つ
PS -> Timer1:set
deactivate PS
activate Timer1
note left Timer1:設定時間\nが経過
PS -> Timer1:timeout
deactivate Timer1
activate PS
note over PS: 非同期サーバソケットを作り\n\
クライアントからの接続を待つ
```

2．第2サーバのみが動いているとき
```plantuml
hide unlinked
control "タイマー 1" as Timer1
participant 第1サーバ as PS
participant 第2サーバ as SS
control "タイマー 2" as Timer2
activate SS
note over SS:非同期ソケットを作り，\n\
第1サーバに接続する

PS <- SS:connect
SS -> Timer2:set
deactivate SS
activate Timer2
note right Timer2:設定時間\nが経過
Timer2 -> SS:timeout
deactivate Timer2
activate SS
note over SS,Timer2: 非同期サーバソケットを作り\n\
クライアントからの接続を待つ
```
