package example.effective.tools;

import io.netty.util.Recycler;

/**
 * Recycler 对象回收站，减少GC压力
 */
public class RecyclerDemo {
    //创建对象，可以用Recycler进行回收利用
    private static final Recycler<User> RECYCLER = new Recycler<User>() {
        @Override
        protected User newObject(Handle<User> handle) {
            return new User(handle);
        }
    };

    static class User {
        private final Recycler.Handle<User> handle;

        public User(Recycler.Handle<User> handle) {
            this.handle = handle;
        }

        //对自身进行回收
        public void recycle() {
            handle.recycle(this);
        }
    }

    public static void main(String[] args) {
        //从回收站里 获取对象
        User user1 = RECYCLER.get();
        //回收对象
        user1.recycle();

        User user2 = RECYCLER.get();
        user2.recycle();

        //true，说明对象被回收再利用了
        System.out.println(user1 == user2);
    }
}
