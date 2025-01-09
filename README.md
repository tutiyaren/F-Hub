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

1.リポジトリのクローンとDBサービス起動

```
git clone git@github.com:tutiyaren/F-Hub.git
```

必要であれば`mv`でディレクトリ名を変える  
上記コマンド後、クローンしたディレクトリに移動する  
docker で必要なサービスを起動

```
cd spring/fhub-feeling
docker compose up -d --build
```

### 結果.ブラウザで確認

- [Laravel ウェルカムページ](http://localhost:8081) にアクセスし、問題なく表示されれば OK です。
- [pgAdmin](http://localhost:8081) にアクセスし、``docker-compose.yml``のpasswordを入力し問題なく表示されれば OK です。  
