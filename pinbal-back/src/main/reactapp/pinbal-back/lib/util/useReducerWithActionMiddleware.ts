import React from 'react';

type MiddlewareFn<T> = (
    state: any,
    action: T,
    errorHandler?: (err: any) => void
) => T | Promise<T> | undefined;

/*
 * Permet emprar el hook useReducer de React especificant un actionMiddleware. A diferencia dels
 * middlewares "normals", els actionMiddlewares retornen l'acció modificada que després s'utilitzarà
 * per a fer el dispatch.
 * Si el middleware no retorna res es farà el dispatch de l'acció original.
 */
export const useReducerWithActionMiddleware = <T>(
    reducer: (state: any, action: T) => any,
    initialState: any,
    middleware?: MiddlewareFn<T>,
    errorHandler?: (err: any) => void
): [any, (action: T) => void] => {
    const [state, dispatch] = React.useReducer(reducer, initialState);
    const stateRef = React.useRef(undefined);
    React.useEffect(() => {
        stateRef.current = state;
    }, [state]);
    const dispatchWithMiddleware = React.useCallback(
        (action: T) => {
            const middlewareAction = middleware?.(stateRef.current, action);
            if (middlewareAction instanceof Promise) {
                middlewareAction
                    .then((middlewareAction: T) => {
                        dispatch(middlewareAction ?? action);
                    })
                    .catch((err) => {
                        errorHandler?.(err);
                    });
            } else {
                dispatch(middlewareAction ?? action);
            }
        },
        [middleware]
    );
    return [state, dispatchWithMiddleware];
};

export default useReducerWithActionMiddleware;
