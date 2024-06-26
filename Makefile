SRC_DIR := nic225
BIN_DIR := bin
LIB_DIR := lib
MAIN_CLASS := App
CLASSPATH := $(LIB_DIR)/ojdbc11.jar:$(BIN_DIR)

all: compile run

compile:
	javac -cp $(CLASSPATH) -d $(BIN_DIR) $(SRC_DIR)/*.java $(SRC_DIR)/other/*.java

run:
	java -cp $(CLASSPATH) $(MAIN_CLASS)

clean:
	rm -rf $(BIN_DIR)
