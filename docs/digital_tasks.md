The Blink Receipt Digital Sdk heavily leverages Google's [Task](https://developers.google.com/android/guides/tasks) for result and exception handling when interfacing with the sdk. Here is a high level comprehensive guide on how to use the Task framework.

The `Task<T>` object, returned by most functions in the sdk, can be thought of as a reference to a job being done either on the main thread or a background thread. The `T` represents the type of result that task is expected to return when done executing it's logic.

## Handling Results From A Task <a name=results></a>

When you have a Task, you are able to apply different sets of listeners that will receive your result or catch your exceptions. There are 2 ways to approach this result handling

### First Way (Recommended) <a name=recommended_way></a>

A successful callback listener, `OnSuccessListener<? super TResult>`, can be added to the task by calling `task.addOnSuccessListener(OnSuccessListener<? super TResult>)`. This listener has a single method `onSuccess(T result)` that needs to be implemented. This listener callback will be invoked upon a successful completion of a given Task.


=== "Java"
    ```java
        Task<Foo> exampleTask = repository.fetchFoo();
    
        exampleTask.addOnSuccessListener( new OnSuccessListener<Foo>() {
            @Override
            public void onSuccess(Foo foo) {
                // Do something
            }
        } );
    ```
=== "Kotlin"
    ```kotlin
        val exampleTask: Task<Foo> = repository.fetchFoo();
    
        exampleTask.addOnSuccessListener { foo -> // Do something }
    ```

Unfortunately, as we know all too well, things do not always go as planned. Exceptions can occur and it is important that we handle those scenarios in the event we wish to provide some sort of recourse for the user. There is a compliment listener that can be added to a `Task` for exception handling. This listener is called a `OnFailureListener`, and it can be added by calling `task.addOnFailureListener(OnFailureListener listener)`. This also has one method, `onFailure(@NonNull Exception e)`, that needs to be implemented. This callback will be invoked in the event any exception is thrown by the task itself.

=== "Java"
    ```java
        Task<Foo> exampleTask = repository.fetchFoo();
    
        exampleTask.addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                // Do something
            }
        } );
    ```
=== "Kotlin"
    ```kotlin
        val exampleTask: Task<Foo> = repository.fetchFoo();
    
        exampleTask.addOnFailureListener { exception -> // Do something }
    ```

Result listeners can be chained for a cleaner look.

=== "Java"
    ```java
        Task<Foo> exampleTask = repository.fetchFoo();
    
        exampleTask.addOnSuccessListener( new OnSuccessListener<Foo>() {
            @Override
            public void onSuccess(Foo foo) {
                // Do something
            }
        } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                // Do something
            }
        } );
    ```

### Second Way (Not Recommended) <a name=unrecommended_way></a>

We believe it is better to separate out the two outcomes of a task, but not all share the same philosophy. This method of result and exception handling combines both eventualities into a single callback, `OnCompleteListener<TResult> listener`. This has a single abstract method which will be implemented `onComplete (Task<TResult> task)`. The invoking of this callback does not signify a successful or a failed task result. Extra logic must be added to make that determination. The `OnCompleteListener` gives you a reference to the task. It is from here that you can do status checks `task.isSuccessful()`, `task.isCompleted()`, or `task.isCanceled()`. The result of the task can be retrieved by calling `task.getResult()`. Alternatively, the thrown exception can be retrieved with a simple `task.getException()` call.

**WARNING** Calling `task.getResult()` while the task is still executing or has already failed will result in a `IllegalStateException` and a `RuntimeException` respectively.
