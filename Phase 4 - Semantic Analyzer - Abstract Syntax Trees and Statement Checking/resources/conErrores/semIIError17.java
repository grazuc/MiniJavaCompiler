///[Error:*|9]
//operador * entre char-int

class X extends Y {

    Y y;

    int m() {
        return (this.y.getX() + 99) * y.x;
    }

    static void main(){}
}

class Y {

    char x;

    int getX(){

    }

}