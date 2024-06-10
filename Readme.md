# S3 server emulator

AWS S3 emulator Supports only REST Path style requests(limited list) https://documenter.getpostman.com/view/34667119/2sA3Qs9rxS



## Properties



```yaml
aws:
  accessKey: String
  secretKey: String
  region: String
  folder: String

server:
  port: Integer
```

```yaml
aws:
  accessKey: ${AWS_ACCESS_KEY:2}
  secretKey: ${AWS_SECRET_ACCESS_KEY:1}
  region: ${AWS_REGION:us-east-1}
  folder: ${AWS_FOLDER:'C:\Users\vsvdev\Desktop\s3server\s3'}
   # folder: ${AWS_FOLDER:~/s3}

server:
  port: ${AWS_PORT:7777}
```

## Build image

mvn clean package

docker build -t vsvdevua/s3server:1.1 .

## Run image

### Unix

```shell
docker run -p 8080:7788 \
  -e AWS_PORT=7788 \
  -e AWS_ACCESS_KEY=3 \
  -e AWS_SECRET_ACCESS_KEY=2 \
  -e AWS_REGION=us-east-1 \
  -e AWS_FOLDER=/app/s3 \
  -v ${PWD}/s3:/app/s3 \
  vsvdevua/s3server:1.1

```


### Windows

```bat
docker run -p 8080:7788 ^
  -e AWS_PORT=7788 ^
  -e AWS_ACCESS_KEY=3 ^
  -e AWS_SECRET_ACCESS_KEY=2 ^
  -e AWS_REGION=us-east-1 ^
  -e AWS_FOLDER=/app/s3 ^
  -v %cd%\s3:/app/s3 ^
  vsvdevua/s3server:1.1


```


```ps1
docker run -p 8080:7788 `
  -e AWS_PORT=7788 `
  -e AWS_ACCESS_KEY=3 `
  -e AWS_SECRET_ACCESS_KEY=2 `
  -e AWS_REGION=us-east-1 `
  -e AWS_FOLDER=/app/s3 `
  -v "${PWD}\s3:/app/s3".Replace("\", "/") `
  vsvdevua/s3server:1.0


```

Example file `.env`

```dotenv
AWS_PORT=7788
AWS_ACCESS_KEY=3
AWS_SECRET_ACCESS_KEY=2
AWS_REGION=us-east-1
AWS_FOLDER=/app/s3

```

```shell
docker run --env-file .env -p 8080:7788 vsvdev/s3server

```

## publish

```shell
docker push vsvdevua/s3server:1.1
```

## Docker hub

https://hub.docker.com/repository/docker/vsvdevua/s3server/general


## Documentation REST

https://documenter.getpostman.com/view/34667119/2sA3Qs9rxS


## Postman collections

bucket_main.postman_collection - examples with params

S3server_doc.postman_collection - docs