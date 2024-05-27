FROM gradle:8.5.0-jdk21

WORKDIR /app

COPY . /app

RUN gradle --no-daemon build

EXPOSE 7070

CMD java -jar build/libs/app-0.0.1-SNAPSHOT.jar