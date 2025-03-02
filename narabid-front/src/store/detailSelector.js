import { createContext, useReducer, useContext } from "react";

// 1. 초기 상태
const initialState = {
  todos: [],
};

// 2. Reducer 함수 정의
const todoReducer = (state, action) => {
  switch (action.type) {
    case "ADD_TODO":
      return { ...state, todos: [...state.todos, action.payload] };
    case "REMOVE_TODO":
      return { ...state, todos: state.todos.filter(todo => todo.id !== action.payload) };
    default:
      return state;
  }
};

// 3. Context 생성
const TodoContext = createContext();

// 4. Provider 컴포넌트
export const TodoProvider = ({ children }) => {
  const [state, dispatch] = useReducer(todoReducer, initialState);

  return (
    <TodoContext.Provider value={{ state, dispatch }}>
      {children}
    </TodoContext.Provider>
  );
};

// 5. 커스텀 훅 (Context 쉽게 사용하기)
export const useTodo = () => useContext(TodoContext);
