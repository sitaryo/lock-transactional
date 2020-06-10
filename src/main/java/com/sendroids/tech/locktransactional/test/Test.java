package com.sendroids.tech.locktransactional.test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Test {

    private final static ExecutorService executorService = Executors.newFixedThreadPool(50);
    private final static HttpClient client =
            HttpClient
                    .newBuilder()
                    .executor(executorService)
                    .build();

    public static void main(String[] args) {

        // 通过 sql for update 实现悲观锁
//        forUpdate();

        // 通过 version 实现乐观锁
//        versionUpdate();

        // 回滚
        exceptionRollBack();

        // 死锁测试
//        deathLock();

        // 锁表
//        lockTable();

        // 锁行
//        lockRow();
    }

    public static void forUpdate() {

        var request = HttpRequest.newBuilder().GET()
                .uri(URI.create("http://127.0.0.1:8080/coder/raise"))
                .header("Content-Type", "application/json")
                .build();
        sendRequestRepeat(request);
    }

    public static void deathLock() {
        var request1 = HttpRequest.newBuilder().GET()
                .uri(URI.create("http://localhost:8080/coder/deathLock/1/2"))
                .header("Content-Type", "application/json")
                .build();
        var request2 = HttpRequest.newBuilder().GET()
                .uri(URI.create("http://localhost:8080/coder/deathLock/2/1"))
                .header("Content-Type", "application/json")
                .build();

        var cf1 = client.sendAsync(request1, HttpResponse.BodyHandlers.ofString());
        var cf2 = client.sendAsync(request2, HttpResponse.BodyHandlers.ofString());
        CompletableFuture.allOf(cf1, cf2)
                .thenRun(executorService::shutdown);

        Stream.of(cf1, cf2)
                .map(CompletableFuture::join)
                .forEach(System.out::println);

    }

    public static void versionUpdate() {
        var request = HttpRequest.newBuilder().GET()
                .uri(URI.create("http://localhost:8080/coder/versionLock"))
                .header("Content-Type", "application/json")
                .build();
        sendRequestRepeat(request);
    }

    private static void sendRequestRepeat(HttpRequest request) {
        AtomicInteger count = new AtomicInteger(0);
        var cfs = IntStream.range(0, 1000)
                .mapToObj((i) -> client.sendAsync(request, HttpResponse.BodyHandlers.ofString()))
                .collect(Collectors.toList());

        CompletableFuture
                .allOf(cfs.toArray(CompletableFuture[]::new))
                .thenRunAsync(() -> {
                    System.out.println("complete");
                    executorService.shutdown();
                });

        cfs.stream()
                .map(CompletableFuture::join)
                .forEach(response ->
                        System.out.println(response.toString() + "-" + count.addAndGet(1))
                );
    }

    public static void exceptionRollBack() {
        var request = HttpRequest.newBuilder().GET()
                .uri(URI.create("http://localhost:8080/coder/exceptionRollBack"))
                .header("Content-Type", "application/json")
                .build();
        var cf = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        cf.thenRun(executorService::shutdown);

        System.out.println(cf.join());

    }

    public static void lockTable() {
        var request1 = HttpRequest.newBuilder().GET()
                .uri(URI.create("http://localhost:8080/coder/tableLock"))
                .header("Content-Type", "application/json")
                .build();
        var request2 = HttpRequest.newBuilder().GET()
                .uri(URI.create("http://localhost:8080/coder/rowLock/2"))
                .header("Content-Type", "application/json")
                .build();

        var cf1 = client.sendAsync(request1, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(s -> {
                    System.out.println(s);
                    return s;
                });

        var cf2 = client.sendAsync(request2, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(s -> {
                    System.out.println(s);
                    return s;
                });

        CompletableFuture.allOf(cf1, cf2)
                .thenRun(executorService::shutdown);

        cf1.join();
        try {
            TimeUnit.SECONDS.sleep(1L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cf2.join();
    }

    public static void lockRow() {
        var request1 = HttpRequest.newBuilder().GET()
                .uri(URI.create("http://localhost:8080/coder/rowLock/1"))
                .header("Content-Type", "application/json")
                .build();
        var request2 = HttpRequest.newBuilder().GET()
                .uri(URI.create("http://localhost:8080/coder/rowLock/2"))
                .header("Content-Type", "application/json")
                .build();

        var cf1 = client.sendAsync(request1, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(s -> {
                    System.out.println(s);
                    return s;
                });
        var cf2 = client.sendAsync(request2, HttpResponse.BodyHandlers.ofString())
                .thenApplyAsync(s -> {
                    System.out.println(s);
                    return s;
                });

        CompletableFuture.allOf(cf1, cf2)
                .thenRun(executorService::shutdown);

        Stream.of(cf1, cf2)
                .forEach(CompletableFuture::join);
    }
}
