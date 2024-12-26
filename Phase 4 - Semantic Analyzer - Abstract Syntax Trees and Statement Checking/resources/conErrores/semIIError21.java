///[Error:return|8]

class A {
    X x;

    void metodo() {
        this.x = new B();
        return this.metodo();
    }
    static void main(){}
}

class B implements Z{

}

interface X {}
interface Y {}
interface Z extends X {}