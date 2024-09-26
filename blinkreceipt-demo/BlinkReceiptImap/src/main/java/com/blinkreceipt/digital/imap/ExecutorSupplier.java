package com.blinkreceipt.digital.imap;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class ExecutorSupplier {

    private static final Object lock = new Object();

    private static volatile ExecutorSupplier sExecutorSupplier;

    private final Executor io;

    private ExecutorSupplier() {
        super();

        io = Executors.newCachedThreadPool();
    }

    @NonNull
    public static ExecutorSupplier getInstance() {
        ExecutorSupplier result = sExecutorSupplier;

        if ( result == null ) {
            synchronized ( lock ) {
                result = sExecutorSupplier;

                if ( result == null ) {
                    sExecutorSupplier = result = new ExecutorSupplier();
                }
            }
        }

        return result;
    }

    @NonNull
    public Executor io() {
        return io;
    }
}
