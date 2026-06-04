import React from 'react';

export type ReactElementWithPosition = {
    position: number;
    element: React.ReactElement;
};

const getPositionedReactElementsWithPosition = (
    nodes: ReactElementWithPosition[] | undefined,
    position: number,
    includeGreaterPosition?: boolean
): ReactElementWithPosition[] | undefined => {
    return nodes?.filter((n) =>
        includeGreaterPosition ? n.position >= position : n.position == position
    );
};

const joinReactElementsWithPosition = (
    numElements: number,
    elementsWithPosition: ReactElementWithPosition[] | undefined
): Record<number, ReactElementWithPosition[]> => {
    const numPositions = numElements + 1;
    const elementsWithPositionToInsert: Record<number, ReactElementWithPosition[]> = {};
    for (let i = numPositions - 1; i >= 0; i--) {
        const elements = getPositionedReactElementsWithPosition(
            elementsWithPosition,
            i,
            i == numElements
        );
        if (elements?.length) elementsWithPositionToInsert[i] = elements;
    }
    return elementsWithPositionToInsert;
};

const toElementsWithPosition = (
    elementsWithPosition: ReactElementWithPosition[] | undefined,
    position: number
): ReactElementWithPosition[] | undefined => {
    return elementsWithPosition?.map((ep) => ({ position, element: ep.element }));
};

export const joinReactElementsWithReactElementsWithPositions = (
    elements: React.ReactElement[],
    elementsWithPosition: ReactElementWithPosition[] | undefined,
    addKeys?: boolean
): React.ReactElement[] => {
    const elementsWithPositionToInsert = joinReactElementsWithPosition(
        elements.length,
        elementsWithPosition
    );
    for (let i = elements.length; i >= 0; i--) {
        const epti = elementsWithPositionToInsert[i];
        if (epti != null) {
            const eti = epti.map((e) => e.element);
            elements.splice(i, 0, ...eti);
        }
    }
    return addKeys ? elements.map((e, i) => React.cloneElement(e, { key: '' + i })) : elements;
};

export const joinReactElementsWithPositionWithReactElementsWithPositions = (
    numElements: number,
    existingElementsWithPosition: ReactElementWithPosition[],
    newElementsWithPosition: ReactElementWithPosition[] | undefined
): ReactElementWithPosition[] => {
    const numPositions1 = numElements + 1;
    const elementsToInsert1: Record<number, ReactElementWithPosition[]> = {};
    for (let i = numPositions1 - 1; i >= 0; i--) {
        const elements = getPositionedReactElementsWithPosition(
            existingElementsWithPosition,
            i,
            i == numElements
        );
        if (elements?.length) elementsToInsert1[i] = elements;
    }
    const numElements2 = numElements + existingElementsWithPosition.length;
    const numPositions2 = numElements2 + 1;
    const elementsToInsert2: Record<number, ReactElementWithPosition[]> = {};
    for (let i = numPositions2 - 1; i >= 0; i--) {
        const elements = getPositionedReactElementsWithPosition(
            newElementsWithPosition,
            i,
            i == numElements2
        );
        if (elements?.length) {
            elementsToInsert2[i] = elements;
        }
    }
    const response: ReactElementWithPosition[] = [];
    let j = 0;
    for (let i = 0; i < numPositions1 + 1; i++) {
        const elements1 = elementsToInsert1[i];
        if (elements1?.length) {
            elements1.forEach((e1) => {
                const elements = toElementsWithPosition(elementsToInsert2[j], i);
                if (elements?.length) response.push(...elements);
                response.push(e1);
                j++;
            });
        } else {
            const elements = toElementsWithPosition(elementsToInsert2[j], i);
            if (elements?.length) response.push(...elements);
            j++;
        }
    }
    return response;
};
