# F-Hub 
## 概要 

## 作成目的 

# Spring Boot 
## 使用技術

Java：23  
Spring Boot：3.4.1
Spring Boot DevTools  
Lombok  
Spring Security  
SpringData JPA  
Flyway Migration  
PostgreSQL Driver  
Validation  
Spring Web  
Spring Boot Actuactor  
Spring Cloud  
Spring Boot Test  
PostgreSQL：17.2  
pgAdmin：4.8.14  


## テーブル設計

![テーブル設計](https://github.com/user-attachments/assets/53e5d645-2d3a-485a-805e-a14ec6a7e99e)

## ER 図

![ER図](https://github.com/user-attachments/assets/ad612bd6-d443-49a4-b243-920aaf9eb51b)

## 環境構築

### クローンして環境構築する手順

1.リポジトリのクローンとサービス起動

```
git clone git@github.com:ENGEI-Dev-Team/office-booking-app.git
```

必要であれば`mv`でディレクトリ名を変える  
上記コマンド後、クローンしたディレクトリに移動する  
docker で必要なサービスを起動

```
cd laravel
make up
```

2.プロジェクトの設定
はじめに、composer コマンドを用いて必要なパッケージをインストールします。

```
make bash
cd api
composer install
exit
```

次に、.env ファイルを編集して必要な設定をするために、.env.example をコピーして.env ファイルを作成と編集する

```
cd src/api
cp .env.example .env
```

slack の dev-team チャンネルにピン止めしてある.env を参考に DB=の設定を行ってください  
その後、下記コマンドで、Laravel のアプリケーションキー生成する

```
cd ../../
make bash
cd api
php artisan key:generate
```

### 結果.ブラウザで確認

- [Laravel ウェルカムページ](http://localhost:8081) にアクセスし、問題なく表示されれば OK です。
- [pgAdmin](http://localhost:8080) にアクセスし、問題なく表示されれば OK です。  
  最後に、PHP コンテナ上で postgreSQL とのデータ接続を確認します。  
  下記コマンドを入力し、エラー文がでなければ OK です

```
php artisan migrate
```