///56&64&4&exitosamente

class MainModule{
    static void main(){
        var a = new A();
        System.printIln(a.m1()+4);
        System.printIln(a.entero);
    }
}
class A{
    int entero;

    int m1(){
        var x = 56;
        System.printIln(x);
        return m2(x);
    }

    int m2(int w){
        entero = 4;
        return entero+w;
    }
}