package jp.tomorrowkey.android.rxandroidtest;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static jp.tomorrowkey.android.rxandroidtest.matcher.RegexMatcher.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class RxAndroidTest {

    @Test
    public void test1() throws Exception {
        final StringBuilder sb = new StringBuilder();

        Observable.just(1)
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer i) {
                        sb.append("1");
                        return i;
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        sb.append("2");
                    }

                    @Override
                    public void onCompleted() {
                        sb.append("3");
                    }

                    @Override
                    public void onError(Throwable e) {
                        sb.append("4");
                    }

                });
        sb.append("5");

        assertThat(sb.toString(), is("1235"));
    }

    @Test
    public void test2() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final StringBuilder sb = new StringBuilder();

        Observable.just(1)
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer i) {
                        sleep(500);
                        sb.append("1");
                        return i;
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        sb.append("2");
                    }

                    @Override
                    public void onCompleted() {
                        sb.append("3");
                        latch.countDown();
                    }

                    @Override
                    public void onError(Throwable e) {
                        sb.append("4");
                    }
                });

        sb.append("5");

        latch.await(10, TimeUnit.SECONDS);

        assertThat(sb.toString(), is("1235"));
    }

    @Test
    public void test3() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final StringBuilder sb = new StringBuilder();
        Observable.just(1)
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer i) {
                        sleep(500);
                        sb.append("1");
                        return i;
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        sb.append("2");
                    }

                    @Override
                    public void onCompleted() {
                        sb.append("3");
                        latch.countDown();
                    }

                    @Override
                    public void onError(Throwable e) {
                        sb.append("4");
                    }
                });
        sb.append("5");

        latch.await(10, TimeUnit.SECONDS);

        assertThat(sb.toString(), is("5123"));
    }

    @Test
    public void test4() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);

        final List<String> list = new ArrayList<>();
        Observable.just(1)
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer i) {
                        list.add("1:" + Thread.currentThread().getName());
                        return i;
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        list.add("2:" + Thread.currentThread().getName());
                    }

                    @Override
                    public void onCompleted() {
                        list.add("3:" + Thread.currentThread().getName());
                        latch.countDown();
                    }

                    @Override
                    public void onError(Throwable e) {
                        list.add("4:" + Thread.currentThread().getName());
                    }
                });

        latch.await(10, TimeUnit.SECONDS);

        assertThat(list.size(), is(3));
        assertThat(list.get(0), is(matches("1:RxNewThreadScheduler-\\d")));
        assertThat(list.get(1), is(matches("2:RxNewThreadScheduler-\\d")));
        assertThat(list.get(2), is(matches("3:RxNewThreadScheduler-\\d")));
    }

    @Test
    public void test5() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);

        final List<String> list = new ArrayList<>();
        Observable.just(1)
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer i) {
                        list.add("1:" + Thread.currentThread().getName());
                        return i;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        list.add("2:" + Thread.currentThread().getName());
                    }

                    @Override
                    public void onCompleted() {
                        list.add("3:" + Thread.currentThread().getName());
                        latch.countDown();
                    }

                    @Override
                    public void onError(Throwable e) {
                        list.add("4:" + Thread.currentThread().getName());
                    }
                });

        latch.await(10, TimeUnit.SECONDS);

        assertThat(list.size(), is(3));
        assertThat(list.get(0), is(matches("1:RxNewThreadScheduler-\\d+")));
        assertThat(list.get(1), is("2:main"));
        assertThat(list.get(2), is("3:main"));
    }

    @Test
    public void test6() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);

        final List<String> list = new ArrayList<>();
        Observable.just(1)
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer i) {
                        list.add("1:" + Thread.currentThread().getName());
                        return i;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer i) {
                        list.add("2:" + Thread.currentThread().getName());
                        return i;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        list.add("3:" + Thread.currentThread().getName());
                    }

                    @Override
                    public void onCompleted() {
                        list.add("4:" + Thread.currentThread().getName());
                        latch.countDown();
                    }

                    @Override
                    public void onError(Throwable e) {
                        list.add("5:" + Thread.currentThread().getName());
                    }
                });

        latch.await(10, TimeUnit.SECONDS);

        assertThat(list.size(), is(4));
        assertThat(list.get(0), is("1:main"));
        assertThat(list.get(1), is("2:main"));
        assertThat(list.get(2), is("3:main"));
        assertThat(list.get(3), is("4:main"));
    }

    @Test
    public void test7() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);

        final List<String> list = new ArrayList<>();
        Observable.just(1)
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer i) {
                        list.add("1:" + Thread.currentThread().getName());
                        return i;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer i) {
                        list.add("2:" + Thread.currentThread().getName());
                        return i;
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        list.add("3:" + Thread.currentThread().getName());
                    }

                    @Override
                    public void onCompleted() {
                        list.add("4:" + Thread.currentThread().getName());
                        latch.countDown();
                    }

                    @Override
                    public void onError(Throwable e) {
                        list.add("5:" + Thread.currentThread().getName());
                    }
                });

        latch.await(10, TimeUnit.SECONDS);

        assertThat(list.size(), is(4));
        assertThat(list.get(0), is(matches("1:RxNewThreadScheduler-\\d+")));
        assertThat(list.get(1), is(matches("2:RxNewThreadScheduler-\\d+")));
        assertThat(list.get(2), is(matches("3:RxNewThreadScheduler-\\d+")));
        assertThat(list.get(3), is(matches("4:RxNewThreadScheduler-\\d+")));
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
