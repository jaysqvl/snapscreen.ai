"use client"

import { Button } from "@/components/ui/button"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { FileText, LayoutDashboard, LogIn, LineChart, Sun, Moon, Info, LogOut } from "lucide-react"
import Link from "next/link"
import { useState, useEffect } from "react"
import { User } from "firebase/auth"
import { auth } from "@/lib/firebase"
import { signOut } from "@/lib/auth"
import { useTheme } from "next-themes"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"

export function AppNavbar() {
  const [user, setUser] = useState<User | null>(null)
  const { theme, setTheme } = useTheme()
  const [mounted, setMounted] = useState(false)
  
  // After mounting, we can access the theme
  useEffect(() => {
    setMounted(true)
  }, [])
  
  useEffect(() => {
    // Set up Firebase auth state listener
    const unsubscribe = auth.onAuthStateChanged((authUser) => {
      setUser(authUser)
    })
    
    // Clean up subscription
    return () => unsubscribe()
  }, [])

  const toggleTheme = () => {
    setTheme(theme === "dark" ? "light" : "dark")
  }

  const handleSignOut = async () => {
    await signOut()
  }

  return (
    <header className="sticky top-0 z-50 w-full border-b bg-card">
      <div className="flex h-14 items-center px-4 md:px-6">
        <Link href="/" className="flex items-center gap-2">
          <FileText className="h-5 w-5" />
          <span className="font-semibold">SnapScreen</span>
        </Link>
        <div className="ml-4 flex items-center gap-2">
          <Button variant="ghost" size="sm" asChild>
            <Link href="/dashboard">
              <LayoutDashboard className="mr-2 h-4 w-4" />
              Dashboard
            </Link>
          </Button>
          <Button variant="ghost" size="sm" asChild>
            <Link href="/dashboard/progress">
              <LineChart className="mr-2 h-4 w-4" />
              Progress
            </Link>
          </Button>
        </div>
        <div className="ml-auto flex items-center gap-4">
          {/* Process button */}
          <Button variant="ghost" size="sm" asChild>
            <Link href="/process">
              <Info className="mr-2 h-4 w-4" />
              Process
            </Link>
          </Button>
          
          {/* Theme toggle button */}
          <Button 
            variant="ghost" 
            size="icon" 
            onClick={toggleTheme}
            aria-label="Toggle theme"
            className="rounded-full"
          >
            {mounted && theme === "dark" ? (
              <Sun className="h-[1.2rem] w-[1.2rem]" />
            ) : (
              <Moon className="h-[1.2rem] w-[1.2rem]" />
            )}
          </Button>
          
          {user ? (
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" className="relative h-8 w-8 rounded-full">
                  <Avatar className="h-8 w-8">
                    <AvatarImage src={user.photoURL || ""} alt={user.displayName || "User"} />
                    <AvatarFallback>{user.displayName?.[0] || user.email?.[0] || "U"}</AvatarFallback>
                  </Avatar>
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent className="w-56" align="end" forceMount>
                <DropdownMenuLabel className="font-normal">
                  <div className="flex flex-col space-y-1">
                    <p className="text-sm font-medium leading-none">{user.displayName || "User"}</p>
                    <p className="text-xs leading-none text-muted-foreground">
                      {user.email}
                    </p>
                  </div>
                </DropdownMenuLabel>
                <DropdownMenuSeparator />
                <DropdownMenuItem onClick={handleSignOut} className="cursor-pointer">
                  <LogOut className="mr-2 h-4 w-4" />
                  Log out
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          ) : (
            <Button size="sm" asChild>
              <Link href="/auth">
                <LogIn className="mr-2 h-4 w-4" />
                Sign In
              </Link>
            </Button>
          )}
        </div>
      </div>
    </header>
  )
} 