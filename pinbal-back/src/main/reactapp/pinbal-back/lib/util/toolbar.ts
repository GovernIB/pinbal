const getImageGradient = (headerColor: string, background: string) => {
    const gradient =
        'linear-gradient(to right, ' +
        headerColor +
        'FF, ' +
        headerColor +
        '00 50% 50%, ' +
        headerColor +
        'FF),';
    return gradient + `url(${background})`;
};

export const toolbarBackgroundStyle = (rgbColor: string, backgroundImg?: string) => {
    const gradientProps = backgroundImg
        ? {
              backgroundImage: getImageGradient(rgbColor, backgroundImg),
              backgroundPosition: 'center',
              backgroundSize: 'cover',
          }
        : null;
    return {
        ...gradientProps,
        backgroundColor: rgbColor,
    };
};
