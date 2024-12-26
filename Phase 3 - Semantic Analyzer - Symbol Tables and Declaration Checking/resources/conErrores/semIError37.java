///[Error:T|11]

//test de implementacion de metodos de interfaces
//la clase B no implementa todos los metodos de la interface T

class A {
    static void main(){}
}


class B implements T{

}


interface T extends U { }
interface U extends Y {}
interface W {}
interface V extends X {}
interface X{}
interface Y{
    void metodo_en_y();
}