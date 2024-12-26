///[SinErrores]
class G {
    boolean b() {
        return true;
    }
}

class A {
    void m() {
        if((new G()).b()) {
            //toGustavo()
        }
    }
}

class Main {
    static void main() {}
}