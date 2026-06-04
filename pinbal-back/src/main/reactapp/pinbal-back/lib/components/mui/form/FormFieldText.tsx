import React from 'react';
import TextField, { TextFieldProps } from '@mui/material/TextField';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import InputAdornment from '@mui/material/InputAdornment';
import { useDebounce } from '../../../util/useDebounce';
import { FormFieldCustomProps } from '../../form/FormField';
import { FormFieldError } from '../../form/FormContext';

type FormFieldTextProps = FormFieldCustomProps & {
    /** Indica si aquest camp és de tipus password */
    password?: true;
    /** Indica si s'ha de deshabilitar el debounce amb els valors del camp */
    debounceDisabled?: true;
};

export const useFormFieldCommon = (
    field: any,
    fieldError: FormFieldError | undefined,
    inline: boolean | undefined,
    componentProps: any,
    startAdornmentIcons?: React.ReactElement[]
) => {
    const title = inline ? (fieldError?.message ?? field?.title) : field?.title;
    const inlineErrorIconElement =
        fieldError && inline ? (
            <Icon fontSize="small" color="error" title={title} sx={{ mr: 1 }}>
                warning
            </Icon>
        ) : null;
    const startAdornment =
        inlineErrorIconElement || startAdornmentIcons?.length ? (
            <>
                {inlineErrorIconElement}
                {...startAdornmentIcons ?? []}
                {componentProps?.slotProps?.input?.startAdornment}
            </>
        ) : (
            componentProps?.slotProps?.input?.startAdornment
        );
    const helperText = inline ? field?.helperText : (fieldError?.message ?? field?.helperText);
    return {
        helperText,
        title,
        startAdornment,
    };
};

const InnerFormFieldText: React.FC<
    FormFieldTextProps & {
        overrideTextFieldProps?: TextFieldProps;
    }
> = (props) => {
    const {
        name,
        label,
        value,
        type,
        field,
        fieldError,
        inline,
        required,
        disabled,
        readOnly,
        onChange,
        componentProps,
        overrideTextFieldProps,
        password,
    } = props;
    const [passwordVisible, setPasswordVisible] = React.useState<boolean>(false);
    const { helperText, title, startAdornment } = useFormFieldCommon(
        field,
        fieldError,
        inline,
        componentProps
    );
    const endAdornment = (
        <>
            {componentProps?.slotProps?.input?.endAdornment}
            {password && (
                <InputAdornment position="end">
                    <IconButton
                        disabled={disabled || readOnly}
                        onClick={() => setPasswordVisible((v) => !v)}
                        size="small">
                        <Icon fontSize="small">
                            {passwordVisible ? 'visibility_off' : 'visibility'}
                        </Icon>
                    </IconButton>
                </InputAdornment>
            )}
        </>
    );
    const inputProps = {
        readOnly,
        ...componentProps?.slotProps?.input,
        startAdornment,
        endAdornment,
    };
    const htmlInputProps = {
        maxLength: field?.maxLength,
        ...componentProps?.slotProps?.htmlInput,
    };
    const isTextAreaType = type === 'textarea' || field?.type === 'textarea';
    return (
        <TextField
            name={name}
            label={!inline ? label : undefined}
            placeholder={componentProps?.placeholder ?? (inline ? label : undefined)}
            value={value ?? ''}
            required={required ?? field?.required}
            disabled={disabled}
            type={password && !passwordVisible ? 'password' : undefined}
            error={fieldError != null}
            title={title}
            onChange={(e) => onChange(e.target.value === '' ? null : e.target.value)}
            fullWidth
            multiline={isTextAreaType}
            rows={isTextAreaType ? 4 : undefined}
            {...componentProps}
            helperText={helperText ?? componentProps.helperText}
            slotProps={{
                input: inputProps,
                htmlInput: htmlInputProps,
            }}
            {...overrideTextFieldProps}
        />
    );
};

const useIsUserTypingRef = (delay: number = 250): [React.RefObject<boolean>, () => void] => {
    const isUserTypingRef = React.useRef(false);
    const timeoutIdRef = React.useRef<any>(null);
    const onUserInput = () => {
        isUserTypingRef.current = true;
        if (timeoutIdRef.current != null) {
            clearTimeout(timeoutIdRef.current);
        }
        timeoutIdRef.current = setTimeout(() => {
            isUserTypingRef.current = false;
        }, delay);
    };
    return [isUserTypingRef, onUserInput];
};

const InnerFormFieldTextDebounce: React.FC<FormFieldTextProps> = (props) => {
    const { value, onChange } = props;
    const [localValue, setLocalValue] = React.useState<string | null>(value);
    const changedValue = useDebounce(localValue, undefined, true);
    const [isUserTypingRef, onUserInput] = useIsUserTypingRef();
    React.useEffect(() => {
        if (!isUserTypingRef.current) {
            setLocalValue(value);
        }
    }, [value]);
    React.useEffect(() => {
        onChange?.(changedValue);
    }, [changedValue]);
    return (
        <InnerFormFieldText
            {...props}
            overrideTextFieldProps={{
                value: localValue ?? '',
                onChange: (e) => {
                    onUserInput();
                    setLocalValue(e.target.value === '' ? null : e.target.value);
                },
            }}
        />
    );
};

export const FormFieldText: React.FC<FormFieldTextProps> = (props) => {
    if (!props.debounceDisabled) {
        return <InnerFormFieldTextDebounce {...props} />;
    } else {
        return <InnerFormFieldText {...props} />;
    }
};

export default FormFieldText;
