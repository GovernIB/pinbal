import { Link } from 'react-router-dom';

export type LinkToTabProps = {
    id: string;
    tab: number;
    children: React.ReactNode;
};

const LinkToTab = (props: LinkToTabProps) => {
    const { id, tab, children } = props;
    const targetUrl = `form/${id}?tab=${tab}`;

    return (
        <Link
            to={targetUrl}
            onClick={(event) => event.stopPropagation()}
            style={{
                width: '100%',
                height: '100%',
                display: 'flex',
                justifyContent: 'flex-end',
                alignItems: 'center',
                textDecoration: 'none',
            }}
        >
            {children}
        </Link>
    );
};

export default LinkToTab;
