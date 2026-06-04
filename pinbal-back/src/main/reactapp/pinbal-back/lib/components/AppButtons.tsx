import { useBaseAppContext, DialogButton } from './BaseAppContext';

export const useAnswerRequiredDialogButtons = () => {
    const { t } = useBaseAppContext();
    return (trueFalseAnswerRequired: boolean, availableAnswers?: string[]) =>
        trueFalseAnswerRequired
            ? [
                  {
                      value: false,
                      text: t('buttons.answerRequired.cancel'),
                      componentProps: { variant: 'outlined' },
                  },
                  {
                      value: true,
                      text: t('buttons.answerRequired.accept'),
                      icon: 'check',
                      componentProps: { variant: 'contained' },
                  },
              ]
            : availableAnswers
              ? availableAnswers.map((a: string) => ({ value: a, text: a }))
              : [
                    {
                        value: true,
                        text: t('buttons.answerRequired.accept'),
                        icon: 'check',
                        componentProps: { variant: 'contained' },
                    },
                ];
};

export const useMessageDialogButtons = (): DialogButton[] => {
    const { t } = useBaseAppContext();
    return [
        {
            value: true,
            text: t('buttons.confirm.accept'),
            icon: 'check',
            componentProps: { variant: 'contained' },
        },
    ];
};

export const useConfirmDialogButtons = (): DialogButton[] => {
    const { t } = useBaseAppContext();
    return [
        {
            value: false,
            text: t('buttons.confirm.cancel'),
            componentProps: { variant: 'outlined' },
        },
        {
            value: true,
            text: t('buttons.confirm.accept'),
            icon: 'check',
            componentProps: { variant: 'contained' },
        },
    ];
};

export const useCloseDialogButtons: () => DialogButton[] = () => {
    const { t } = useBaseAppContext();
    return [
        {
            value: false,
            text: t('buttons.misc.close'),
            componentProps: { variant: 'contained' },
        },
    ];
};

export const useFormDialogButtons: () => DialogButton[] = () => {
    const { t } = useBaseAppContext();
    return [
        {
            value: false,
            text: t('buttons.form.cancel'),
            componentProps: { variant: 'outlined' },
        },
        {
            value: true,
            text: t('buttons.form.save'),
            icon: 'save',
            componentProps: { variant: 'contained' },
        },
    ];
};

export const useActionDialogButtons: () => DialogButton[] = () => {
    const { t } = useBaseAppContext();
    return [
        {
            value: false,
            text: t('buttons.action.cancel'),
            componentProps: { variant: 'outlined' },
        },
        {
            value: true,
            text: t('buttons.action.exec'),
            icon: 'bolt',
            componentProps: { variant: 'contained' },
        },
    ];
};

export const useReportDialogButtons: () => DialogButton[] = () => {
    const { t } = useBaseAppContext();
    return [
        {
            value: false,
            text: t('buttons.report.cancel'),
            componentProps: { variant: 'outlined' },
        },
        {
            value: true,
            text: t('buttons.report.generate'),
            icon: 'summarize',
            componentProps: { variant: 'contained' },
        },
    ];
};
