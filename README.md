## Synopsis

APIDB dibuat untuk memudahkan komunikasi dengan Database menggunakan API

## Code Example
  ```
  type 1
  http://localhost:9002/querylist?jndi=erin&sql=select * from user
  ```
  ```
  result:
  {"status":"","desc":"","count":2,"count_total":2,"pages":1,"results":[{"password":"6ad14ba9986e3615423dfca256d04e3f","phone":"08118003585","last_update":"2017-06-28 20:34:50","id":"2","type":"0","email":"qomarullah.mail@gmail.com","username":"user"},{"password":"6ad14ba9986e3615423dfca256d04e3f","phone":"08118003585","last_update":"2017-06-28 20:34:50","id":"2","type":"0","email":"qomarullah.mail@gmail.com","username":"user"}]}
 ```
  ```
  type 2
  http://localhost:9002/getlist?jndi=hvc&sqlid=1&msisdn=628111111111
  ```
  ```
  result:
  {"status":"success","desc":"","count":1,"count_total":1,"pages":1,"results":[{"PP":"PP Loop Charlie","LOS":"107","BCP_SEGMENT":"DeviceServices|Net|SocialNet|Web|Ads","REV":"0.00","RECHARGE_M":"10","DOMINANT_CITY":"null","MSISDN":"6282213801053","REDEEM_CATEGORY":"null","LAST_UPDATED":"2017-04-10 00:00:00.0","ARPU":"0.00","RECHARGE_M1":"100","LOYALTY_SEGMENT":"null"}]}
  ```
  etc..
  
## Motivation

Sebelum muncul ide microservice, aplikasi umumnya dibuat monolithic dimana setiap logic ditanam di satu apps-server yang besar.
termasuk didalamnya komunikasi dengan 1 atau banyak database menggunakan koneksi pooling. 
Pada saat apps-server membutuhkan scalling untuk traffic yang lebih besar, akan dibutuhakn pula penambahan koneksi ke Database, 
padahal kebutuhan serve transaksi oleh apps-server tidak equivalen dgn kebutuhan koneksi ke DB.

Dengan adanya APIDB, komunikasi ke DB bisa dihandle oleh 1 apps-server yang juga sejalan dengan ide microservice, yakni memecah
kebutuhan resource dengan API sehingga agile utk development, efesiensi resource dll


## Installation

1. Silahkan download atau clone project
2. Ubah configurasi (config dan path log)
3. run project

## API Reference

Project ini menggunakan beberapa library:
- http://sparkjava.com/ , untuk rest API handler
- http://www.mchange.com/projects/c3p0/, untuk JDBC pooling
- log4j2, untuk logging


## Tests

Describe and show how to run the tests with code examples.

## Contributors

Let people know how they can dive into the project, include important links to things like issue trackers, irc, twitter accounts if applicable.

## License

A short snippet describing the license (MIT, Apache, etc.)
