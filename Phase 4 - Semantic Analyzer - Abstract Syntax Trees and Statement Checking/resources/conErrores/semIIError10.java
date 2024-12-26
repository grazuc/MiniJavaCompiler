///[Error:this|7]
//uso de this dentro de un metodo con alcance estatico
class A {

    int y;
    static void metodoEstatico() {
        this.m1(y);
    }
    static void main(){}
}