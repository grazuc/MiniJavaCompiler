///5&5&5&6&6&6&exitosamente


class A{
    int at1;
    static void main(){
        new A().init(5).m1().m1();
        new A().init(6).m1().m1();
    }

    A init(int i){
        at1 = i;
        this.m1();
        return this;
    }

    A m1(){
        System.printIln(at1);
        return this;
    }
}