import React from 'react';
import './App.css';
import {Messenger} from "./components/messenger/Messenger";
import {createTheme, ThemeProvider} from "@mui/material";
import 'react-perfect-scrollbar/dist/css/styles.css';
import * as yup from "yup";
import Notifier from './Notifier';
import {store} from "./index";
import {RootStoreProvider} from "./store/provider/RootStoreProvider";

yup.addMethod(yup.mixed, "isGlobalUserNotExists", function (errorMessage) {
    return this.test('test.ts-global-user-existing', errorMessage, function (value) {
        const {path, createError} = this;
        const globalUser = store.getState().messenger.globalUsers[value!];

        return (!globalUser || createError({path, message: errorMessage}));
    })
})

function App() {
    const theme = createTheme({
        palette: {
            mode: 'dark',
        }
    });

    return (
        <ThemeProvider theme={theme}>
            <RootStoreProvider>
                <Notifier/>
                <Messenger/>
            </RootStoreProvider>
        </ThemeProvider>
    );
}

export default App;
