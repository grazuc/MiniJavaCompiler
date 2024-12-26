///99&exitosamente

class A{
    int x;
    static void main(){
        var a = new A();
        var b = new B();
        b.m1(99);
        System.printIln(b.getX());
    }

    int getX(){
        return x;
    }

    void m1(int i){
        x = i;
    }

}

class B extends A{

    void m1(int i){
        x = i;
    }

    int getX(){
        return x;
    }
}